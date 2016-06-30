package com.supinfo.transcode.interfaces.normal.job;

import java.util.Collection;

import com.supinfo.transcode.entity.User;
import com.supinfo.transcode.entity.Worker;

import javassist.NotFoundException;

public interface IWorkerJob {
	public Worker addWorker(Worker worker, int id) throws NotFoundException ;
	public Worker updateWorker(Worker workerUpdate,Worker worker);
	public void deleteWorker(int id);
	public Worker getWorkerById(int id);
	public Worker getWorkerByIp(String ip);
	public Collection<Worker> getWorkersByPoolId(int id);
}
