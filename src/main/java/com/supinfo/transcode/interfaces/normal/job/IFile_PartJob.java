package com.supinfo.transcode.interfaces.normal.job;

import java.util.List;

import com.supinfo.transcode.entity.File;
import com.supinfo.transcode.entity.File_part;

import javassist.NotFoundException;

public interface IFile_PartJob {
	public void addFile_Part(File f, int number) throws NotFoundException;
	public void updateFile_Part(File_part file_partUpload,File_part file_part);
	public List<File_part> partsByFile(int id);
}
