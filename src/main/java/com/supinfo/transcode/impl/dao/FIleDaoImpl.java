package com.supinfo.transcode.impl.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.supinfo.transcode.entity.File;
import com.supinfo.transcode.entity.File_part;
import com.supinfo.transcode.entity.Queue;
import com.supinfo.transcode.entity.Streaming;
import com.supinfo.transcode.interfaces.global.dao.IGlobalFileDao;

public class FIleDaoImpl implements IGlobalFileDao {
	@PersistenceContext
	EntityManager em;

	@Override
	public void addFile(File file) {
		em.persist(file);
	}

	@Override
	public void updateFile(File file) {
		em.merge(file);
	}

	@Override
	public void addFile_Part(File_part file_part) {
		em.persist(file_part);
	}

	@Override
	public void updateFile_Part(File_part file_part) {
		em.merge(file_part);
	}
	
	@Override
	public File getLastFile(){
		Query req= em.createQuery("SELECT f FROM  File f WHERE f.id = (SELECT MAX(f.id)  FROM File f)");
		return (File) req.getSingleResult();	
	}

	@Override
	public File getFileById(int id) {
		return em.find(File.class, id);
	}

	@Override
	public File_part getFilePartByNameFileId(String name,int id) {
		Query req= em.createQuery("SELECT f FROM  File_part f WHERE f.name = '"+name+"' AND f.file.id = "+id+"");
		return (File_part) req.getSingleResult();
	}

	@Override
	public List<File_part> partsByFile(int id) {
		try {
			Query req= em.createQuery("SELECT f FROM  File_part f WHERE f.file.id = "+id+"");	
			if(req.getResultList().size() == 0){
				return null;
			}else{
				return req.getResultList();
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Queue getFirstQueue() {
		try {
			Query req= em.createQuery("SELECT q FROM  Queue q WHERE q.status = 0");	
			if(req.getResultList().size() == 0){
				return null;
			}else{
				return (Queue) req.getResultList().get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Collection<Streaming> getAllStreamByUser(String username) {
		try {
			Query req= em.createQuery("SELECT s FROM  Streaming s WHERE s.user.username = '"+username+"'");	
			return req.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Integer getLastIdStreaming() {
		try {
			Query req= em.createQuery("SELECT s FROM  Streaming s WHERE s.id = (SELECT MAX(s.id)  FROM Streaming s)");	
			Streaming stream = (Streaming) req.getSingleResult();
			return stream.getId();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void insertStream(Streaming stream) {
		em.persist(stream);
	}

	@Override
	public void updateStream(Streaming stream) {
		em.merge(stream);
	}
}
