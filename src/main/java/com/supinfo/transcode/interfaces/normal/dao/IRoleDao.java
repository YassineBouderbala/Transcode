package com.supinfo.transcode.interfaces.normal.dao;

import com.supinfo.transcode.entity.Role;

public interface IRoleDao {
	public Role getRoleById(int id);
	public void deleteRoleById(int id);
	public void updateRole(Role role);
	public void addRole(Role role);
}
