package com.supinfo.transcode.interfaces.normal.job;

import com.supinfo.transcode.entity.User;

public interface IUserJob {
	public User getUserById(int id);
	public User getUserByUsername(String username);
	public User getUserByEmail(String email);
	public void deleteUser(int id);
	public User updateUser(User user,User userToUpdatUser);
	public User insertUser(User user);
}
