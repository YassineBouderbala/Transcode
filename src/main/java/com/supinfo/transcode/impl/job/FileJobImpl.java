package com.supinfo.transcode.impl.job;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.supinfo.transcode.entity.File;
import com.supinfo.transcode.entity.File_part;
import com.supinfo.transcode.entity.Pool;
import com.supinfo.transcode.entity.Queue;
import com.supinfo.transcode.entity.Streaming;
import com.supinfo.transcode.interfaces.global.dao.IGlobalFileDao;
import com.supinfo.transcode.interfaces.global.dao.IGlobalPoolDao;
import com.supinfo.transcode.interfaces.global.dao.IGlobalUserDao;
import com.supinfo.transcode.interfaces.global.job.IGlobalFileJob;
import com.supinfo.transcode.security.Mail;
import com.supinfo.transcode.utils.FileLink;
import com.supinfo.transcode.utils.ssh.SshClient;
import com.supinfo.transcode.utils.ssh.ThreadSsh;

import javassist.NotFoundException;

@Transactional
public class FileJobImpl implements IGlobalFileJob {
	
	private IGlobalFileDao dao;
	private IGlobalUserDao dao_user;
	private IGlobalPoolDao dao_pool;
	
	public void setDao(IGlobalFileDao dao){
		this.dao = dao;
	}
	
	public void setDao_user(IGlobalUserDao dao){
		this.dao_user = dao;
	}
	
	public void setDao_pool(IGlobalPoolDao dao){
		this.dao_pool = dao;
	}
	
	@Override
	public File addFile(String username,File file,MultipartFile f) {
		File ff = new File();
		if (!f.isEmpty()) {
			try {
				File fileInsert =  new File();
				int id = 0;
				
	            if(this.getLastFile().getId() != 0){
	            	id += this.getLastFile().getId() + 1;
	            }else{
	                id = 1;
	            }
				
                // Creating the directory to store file
				byte[] bytes = f.getBytes();
                java.io.File dir = new java.io.File(new FileLink().getLinkFileUpload()+"/"+id);
                if (!dir.exists())
                    dir.mkdirs();
                               
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(dir+"/"+f.getOriginalFilename()));
                stream.write(bytes);
                stream.close();
                
                String name = f.getOriginalFilename();
                int pos = name.lastIndexOf(".");
                if (pos > 0) {
                    name = name.substring(0, pos);
                }
                
                fileInsert.setName(name);
                fileInsert.setConvertValue(file.getConvertValue());
                fileInsert.setUser(dao_user.getUserByUsername(username));
                                
                Queue queue = new Queue();
                queue.setStatus("0");
                queue.setFile(fileInsert);
                queue.setPool(null);
                
                fileInsert.setQueue(queue);
                
                Pool pool = dao_pool.getPoolUnable();
                
                if(pool != null){
                	
                	pool.setStatus(true);
                	fileInsert.getQueue().setStatus("1");
                	fileInsert.getQueue().setPool(pool);
                	
                	dao.addFile(fileInsert);
                	
                    
                		//LANCE LE PREMIER SCRIPT DE PARTITIONNAGE
                	new ThreadSsh("screen -dm bash /mnt/san/script/split.sh --source "+id+" --pool "+fileInsert.getQueue().getPool().getId()+"").run();
            		new ThreadSsh("screen -dm bash /mnt/san/script/demux.sh "+id+"").run();

                
                	System.out.println("*********** PARTIONNING ************");
                }else{
                	dao.addFile(fileInsert);
                }
                                                
                return null;
			} catch (Exception e) {
				ff.setName("Error uploading file");
				return ff;
			}
		}else{
			ff.setName("File empty");
			return ff;
		}
	}

	@Override
	public File updateFile(File uploadFile, File file) {
		if(file.getQueue().getStatus() != null){
			uploadFile.getQueue().setStatus(file.getQueue().getStatus());
			dao.updateFile(uploadFile);
		}else{
			uploadFile.getQueue().setFinished(file.getQueue().isFinished());
			if(file.getQueue().isFinished() == true){
				uploadFile.getQueue().getPool().setStatus(false);
				Queue q = this.getFirstQueue();
				if(q != null){
					
					q.setStatus("1");
					uploadFile.getQueue().getPool().setStatus(true);
					q.setPool(uploadFile.getQueue().getPool());
					
					dao.updateFile(q.getFile());

					//LANCE LE SCRIPT DE PARTITION
					
            		new ThreadSsh("screen -dm bash /mnt/san/script/split.sh --source "+q.getFile().getId()+" --pool "+q.getFile().getQueue().getPool().getId()+"").run();
            		new ThreadSsh("screen -dm bash /mnt/san/script/demux.sh "+q.getFile().getId()+"").run();

            		
            		System.out.println("*********** PARTITIONNING ************");
				}
				//Mail.send(new FileLink().getUri()+"/file/download/"+uploadFile.getId()+"?username="+uploadFile.getUser().getUsername()+"&password="+uploadFile.getUser().getPassword(), uploadFile.getUser().getEmail());				
			}
			dao.updateFile(uploadFile);			
		}
		return null;
	}

	@Override
	public void addFile_Part(File f,int number) throws NotFoundException {
		if(f == null){
			throw new NotFoundException("Not found");	
		}
		if(number > 0){
			for (int i = 0; i < number; i++) {
				File_part part = new File_part();
				part.setName("part"+i);
				part.setFile(f);
				dao.addFile_Part(part);	
								
			}
			
			System.out.println("*********** TRANSCODING ************");
			
		}else{
			throw new NotFoundException("Not found");
		}
	}

	@Override
	public void updateFile_Part(File_part file_partUpload, File_part file_part) {
		int cmp = 0;
		
		file_partUpload.setStatus(file_part.isStatus());
		dao.updateFile_Part(file_partUpload);
		
		List<File_part> files = dao.partsByFile(file_partUpload.getFile().getId());
		
		if(files != null){
			for (File_part fp : files) {
				if(fp.isStatus() == false){
					cmp++;
					break;
				}
			}			
		}
		
		if(cmp == 0){

			 // LANCE LE SCRIPT DE FUSION DES FICHIERS

    		new ThreadSsh("screen -dm bash /mnt/san/script/fusion.sh "+file_partUpload.getFile().getId()+"").run();		
			
    		System.out.println("FUSION DES FICHIERS");
		}
	}
	
	@Override
	public File getLastFile(){
		File file = new File();
		try {
			File f = dao.getLastFile();
			
			if(f == null){
				file.setId(0);
				return file;
			}
			return f;
		} catch (Exception e) {
			file.setId(0);
			return file;
		}
	}

	@Override
	public File getFileById(int id) {
		try {
			return dao.getFileById(id);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public File_part getFilePartByNameFileId(String name,int id) {
		return dao.getFilePartByNameFileId(name, id);
	}

	@Override
	public List<File_part> partsByFile(int id) {
		return (List<File_part>) dao.partsByFile(id);
	}

	@Override
	public Queue getFirstQueue() {
		return dao.getFirstQueue();
	}

	@Override
	public Collection<Streaming> getAllStreamByUser(String username) {
		return dao.getAllStreamByUser(username);
	}

	@Override
	public Integer getLastIdStreaming() {
		return dao.getLastIdStreaming();
	}

	@Override
	public void insertStream(Streaming stream) {
		dao.insertStream(stream);
	}

	@Override
	public void updateStream(Streaming stream) {
		dao.updateStream(stream);
	}

}
