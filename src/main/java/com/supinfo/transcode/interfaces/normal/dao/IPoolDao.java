package com.supinfo.transcode.interfaces.normal.dao;

import java.util.Collection;

import com.supinfo.transcode.entity.Pool;

public interface IPoolDao {
	public void insertPool(Pool pool);
	public void updatePool(Pool pool);
	public Pool getPoolById(int id);
	public Collection<Pool> getPools();
	public void deletePool(int id);
	public Pool getPoolUnable();
}
