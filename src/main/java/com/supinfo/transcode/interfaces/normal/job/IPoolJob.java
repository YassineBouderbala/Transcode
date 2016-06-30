package com.supinfo.transcode.interfaces.normal.job;

import java.util.Collection;

import com.supinfo.transcode.entity.Pool;

import javassist.NotFoundException;

public interface IPoolJob {
	public Pool insertPool(Pool pool);
	public Pool updatePool(Pool updatePool,Pool pool);
	public Pool getPoolById(int id) throws NotFoundException;
	public Collection<Pool> getPools();
	public void deletePool(int id);
	public Pool getPoolUnable();
}
