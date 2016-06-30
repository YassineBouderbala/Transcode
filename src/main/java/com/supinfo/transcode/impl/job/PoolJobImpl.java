package com.supinfo.transcode.impl.job;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.supinfo.transcode.entity.Pool;
import com.supinfo.transcode.entity.Worker;
import com.supinfo.transcode.interfaces.global.dao.IGlobalPoolDao;
import com.supinfo.transcode.interfaces.global.job.IGlobalPoolJob;

import javassist.NotFoundException;

@Transactional
public class PoolJobImpl implements IGlobalPoolJob {
	
	private IGlobalPoolDao dao;
	
	public void setDao(IGlobalPoolDao dao){
		this.dao = dao;
	}
	
	@Override
	public Pool insertPool(Pool persistancePool) {
		Pool pool = new Pool();
		boolean success = true;
		
		if(persistancePool.getName() != null){
			if(persistancePool.getName().length() == 0){
				pool.setName("Pool name is empty");
				success = false;
			}
		}else{
			pool.setName("Pool name is empty");
			success = false;
		}
		
		if(persistancePool.getPath() != null){
			if(persistancePool.getPath().length() == 0){
				pool.setPath("Path is empty");
				success = false;
			}
		}else{
			pool.setPath("Path is empty");
			success = false;
		}
		
		if(success){
			int cmp = 0;
			for (Pool p : dao.getPools()) {
				cmp++;
			}
			if(cmp == 2){
				pool.setName("Already have two pools created");
				return pool;
			}else{
				dao.insertPool(persistancePool);
				return null;
			}
		}else{
			return pool;
		}
	}

	@Override
	public Pool updatePool(Pool updatePool,Pool pool) {
		Pool p = new Pool();
		boolean success = true;
		
		if(pool.getName() == null && pool.getPath() == null){
			updatePool.setStatus(pool.isStatus());
		}
		
		if(pool.getName() != null){
			if(pool.getName().length() > 0){
				updatePool.setName(pool.getName());
			}else{
				p.setName("Pool name is empty");
				success = false;
			}
		}
		
		if(pool.getPath() != null){
			if(pool.getPath().length() > 0){
				updatePool.setPath(pool.getPath());
			}else{
				p.setPath("Path is empty");
				success = false;
			}
		}
		
		if(success){
			dao.updatePool(updatePool);
			return null;
		}else{
			return p;
		}
	}

	@Override
	public Pool getPoolById(int id) throws NotFoundException {
		Pool pool = dao.getPoolById(id);
		if(pool == null){
			throw new NotFoundException("Not found");
		}else{
			return pool;
		}
	}

	@Override
	public Collection<Pool> getPools() {
		return dao.getPools();
	}

	@Override
	public void deletePool(int id) {
		dao.deletePool(id);
	}

	@Override
	public Worker addWorker(Worker worker,int id) throws NotFoundException {
		Worker w = new Worker();
		boolean success = true;
		
		if(worker.getIp() != null){
			if(worker.getIp().length() == 0){
				w.setIp("Ip is empty");
				success = false;
			}
		}else{
			w.setIp("Ip is empty");
			success = false;
		}
			
		List<Worker> workers = (List<Worker>) this.getWorkersByPoolId(id);
		
		
		if(success){
			if(workers.size() == 2){
				w.setIp("Already have two workers for this pool");	
				return w;
			}else{
				Pool pool = this.getPoolById(id);
				worker.setPool(pool);
				dao.addWorker(worker);
				return null;
			}			
		}else{
			w.setIp("Ip is empty");	
			return w;			
		}

	}

	@Override
	public Worker updateWorker(Worker workerUpdate,Worker worker) {
		Worker w = new Worker();
		boolean success = true;
		
		//if(worker.getIp() != null){
			if(worker.getIp().length() ==  0){
				w.setIp("Ip is empty");
				success = false;
			}else{				
				workerUpdate.setIp(worker.getIp());
				//workerUpdate.setStatus(this.getWorkerById(workerUpdate.getId()).isStatus());
			}
		//}else{
			//workerUpdate.setStatus(worker.isStatus());				
		//}
				
		if(success){
			dao.updateWorker(workerUpdate);
			return null;
		}else{
			return w;			
		}

	}

	@Override
	public void deleteWorker(int id) {
		dao.deleteWorker(id);
	}

	@Override
	public Worker getWorkerById(int id) {
		return dao.getWorkerById(id);
	}

	@Override
	public Worker getWorkerByIp(String ip) {
		return dao.getWorkerByIp(ip);
	}

	@Override
	public Collection<Worker> getWorkersByPoolId(int id) {
		return dao.getWorkersByPoolId(id);
	}

	@Override
	public Pool getPoolUnable() {
		return dao.getPoolUnable();
	}
	
}
