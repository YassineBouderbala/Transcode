package com.supinfo.transcode.interfaces.normal.dao;

import java.util.Collection;
import java.util.stream.Stream;

import com.supinfo.transcode.entity.File;
import com.supinfo.transcode.entity.File_part;
import com.supinfo.transcode.entity.Queue;
import com.supinfo.transcode.entity.Streaming;
import com.supinfo.transcode.entity.User;

public interface IFileDao {
	public File getFileById(int id);
	public Queue getFirstQueue();
	public void addFile(File file);
	public void updateFile(File file);
	public File getLastFile();
	public File_part getFilePartByNameFileId(String name,int id);
	
	public Collection<Streaming> getAllStreamByUser(String username);
	public Integer getLastIdStreaming();
	public void insertStream(Streaming stream);
	public void updateStream(Streaming stream);
}
