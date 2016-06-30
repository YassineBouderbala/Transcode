package com.supinfo.transcode.interfaces.normal.dao;

import java.util.Collection;

import com.supinfo.transcode.entity.User;
import com.supinfo.transcode.entity.Worker;

public interface IWorkerDao {
	public void addWorker(Worker worker);
	public void updateWorker(Worker worker);
	public void deleteWorker(int id);
	public Worker getWorkerById(int id);
	public Worker getWorkerByIp(String ip);
	public Collection<Worker> getWorkersByPoolId(int id);
}
