package com.supinfo.transcode.interfaces.normal.job;

import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

import com.supinfo.transcode.entity.File;
import com.supinfo.transcode.entity.File_part;
import com.supinfo.transcode.entity.Queue;
import com.supinfo.transcode.entity.Streaming;

public interface IFileJob {
	public File addFile(String username,File file,MultipartFile f);
	public File updateFile(File uploadFile,File file);
	public File getLastFile();
	public File getFileById(int id);
	public File_part getFilePartByNameFileId(String name,int id);
	public Queue getFirstQueue();
	
	public Collection<Streaming> getAllStreamByUser(String username);
	public Integer getLastIdStreaming();
	public void insertStream(Streaming stream);
	public void updateStream(Streaming stream);
}
