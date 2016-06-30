package com.supinfo.transcode.impl.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.supinfo.transcode.entity.Pool;
import com.supinfo.transcode.entity.Worker;
import com.supinfo.transcode.interfaces.global.dao.IGlobalPoolDao;

public class PoolDaoImpl implements IGlobalPoolDao{

	
	@PersistenceContext
	EntityManager em;

	@Override
	public void insertPool(Pool pool) {
		em.persist(pool);
	}

	@Override
	public void updatePool(Pool pool) {
		em.merge(pool);
	}

	@Override
	public Pool getPoolById(int id) {
		return em.find(Pool.class, id);
	}

	@Override
	public Collection<Pool> getPools() {
		try {
			Query req = em.createQuery("SELECT p FROM Pool p");
			return req.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void deletePool(int id) {
		em.remove(em.find(Pool.class, id));
	}

	@Override
	public void addWorker(Worker worker) {
		em.persist(worker);		
	}

	@Override
	public void updateWorker(Worker worker) {
		em.merge(worker);
	}

	@Override
	public void deleteWorker(int id) {
		em.remove(em.find(Worker.class, id));
	}

	@Override
	public Worker getWorkerById(int id) {
		return em.find(Worker.class, id);
	}

	@Override
	public Worker getWorkerByIp(String ip) {
		try {
			Query req = em.createQuery("SELECT w FROM Worker w where w.ip = '"+ip+"'");
			return (Worker)req.getSingleResult();
		} catch (Exception e) {
			return null;
		}		
	}

	@Override
	public Collection<Worker> getWorkersByPoolId(int id) {
		try {
			Query req = em.createQuery("SELECT w FROM Worker w where w.pool.id = "+id+"");
			return req.getResultList();
		} catch (Exception e) {
			return null;
		}	
	}

	@Override
	public Pool getPoolUnable() {
		try {
			Query req = em.createQuery("SELECT p FROM Pool p where p.status = 0");
			List<Pool> pools = req.getResultList();
			if(pools.size() == 0){
				return null;
			}else{
				for (Pool pool : pools) {
					return pool;
				}
				throw new Exception();
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	
}
