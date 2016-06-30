package com.supinfo.transcode.interfaces.normal.dao;

import java.util.List;

import com.supinfo.transcode.entity.File_part;

public interface IFile_PartDao {
	public void addFile_Part(File_part file_part);
	public void updateFile_Part(File_part file_part);
	public List<File_part> partsByFile(int id);
}
