package com.supinfo.transcode.interfaces.normal.job;

import com.supinfo.transcode.entity.Role;

public interface IRoleJob {
	public Role getRoleById(int id);
	public void deleteRoleById(int id);
	public Role updateRole(Role role);
	public Role addRole(Role role);
}
