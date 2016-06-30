package com.supinfo.transcode.interfaces.normal.dao;

import com.supinfo.transcode.entity.User;

public interface IUserDao {
	public User getUserById(int id);
	public User getUserByUsername(String username);
	public User getUserByEmail(String email);
	public void deleteUser(int id);
	public void updateUser(User user);
	public void insertUser(User user);
}
