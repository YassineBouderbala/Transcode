package com.supinfo.transcode.impl.job;

import org.springframework.transaction.annotation.Transactional;

import com.supinfo.transcode.entity.Role;
import com.supinfo.transcode.entity.User;
import com.supinfo.transcode.interfaces.global.dao.IGlobalUserDao;
import com.supinfo.transcode.interfaces.global.job.IGlobalUserJob;
import com.supinfo.transcode.security.Crypter;

@Transactional
public class UserJobImpl implements IGlobalUserJob {

	private IGlobalUserDao dao;
	
	public void setDao(IGlobalUserDao dao){
		this.dao = dao;
	}

	@Override
	public User getUserById(int id) {
		return dao.getUserById(id);
	}

	@Override
	public User getUserByUsername(String username) {
		try {
			return dao.getUserByUsername(username);			
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public User getUserByEmail(String email) {
		try {
			return dao.getUserByEmail(email);			
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void deleteUser(int id) {
		dao.deleteUser(id);
	}

	@Override
	public User updateUser(User userUpdated,User user) {
		User u = new User();
		boolean success = true;
		if(user.getFirst_name() != null){
			if(user.getFirst_name().length() > 0){
				userUpdated.setFirst_name(user.getFirst_name());
			}else{	
				u.setFirst_name("First name is empty");
				success = false;
			}
		}
		if(user.getLast_name() != null){
			if(user.getLast_name().length() > 0 ){
				userUpdated.setLast_name(user.getLast_name());				
			}else{
				u.setLast_name("Last name is empty");
				success = false;
			}
		}
		if(user.getEmail() != null){
			if(this.getUserByEmail(user.getEmail()) != null){
				u.setEmail("Email already taken");
				success = false;
			}else if(user.getEmail().length() < 7){
				u.setEmail("It is not an email");
				success = false;
			}
			else{
				userUpdated.setEmail(user.getEmail());			
			}
		}
		if(user.getPassword() != null){
			if(user.getPassword().length() > 0){
				userUpdated.setPassword(Crypter.Md5(userUpdated.getPassword()));				
			}else{
				u.setPassword("Empty field");
				success = false;
			}
		}
		if(success){
			dao.updateUser(userUpdated);
			return null;
		}else{
			return u;
		}
	}

	@Override
	public User insertUser(User u) {
		User user = new User();
		boolean success = true;
		try {
			if(u.getFirst_name().length() == 0){
				user.setFirst_name("Empty Field");
				success = false;
			}else if(u.getFirst_name().length() >= 50){
				user.setFirst_name("Maximum size : 50");
				success = false;
			}			
		} catch (Exception e) {
			user.setFirst_name("Empty Field");
			success = false;
		}

		
		try {
			if(u.getLast_name().length() == 0){
				user.setLast_name("Empty Field");
				success = false;
			}else if(u.getLast_name().length() >= 50){
				user.setLast_name("Maximum size : 50");
				success= false;
			}			
		} catch (Exception e) {
			user.setLast_name("Empty Field");
			success = false;
		}
		
		try {
			if(this.getUserByEmail(u.getEmail()) != null){
				user.setEmail("Email is already taken");
				success = false;
			}
			if(u.getEmail().length() < 7){
				user.setEmail("It is not an email");
				success = false;			
			}			
		} catch (Exception e) {
			user.setEmail("It is not an email");
			success = false;	
		}

		
		if(this.getUserByUsername(u.getUsername()) != null){
			user.setUsername("Username is already taken");
			success = false;
		}
		
		try {
			if(u.getUsername().length() == 0){
				user.setUsername("Empty username");
				success = false;			
			}			
		} catch (Exception e) {
			user.setUsername("Empty username");
			success = false;	
		}
		
		try {
			if(u.getPassword().length() == 0 || !u.getPassword_confirm().equals(u.getPassword())){
				user.setPassword("Passwords not match");
				success = false;
			}			
		} catch (Exception e) {
			user.setPassword("Passwords not match");
			success = false;
		}
		
		if(success){
			Role role = new Role();
			role.setName("ROLE_USER");
			role.setUser(u);
			
			u.getRoles().add(role);
			u.setPassword(Crypter.Md5(u.getPassword()));
			dao.insertUser(u);
			return null;
		}else{
			return user;
		}

	}

	@Override
	public Role getRoleById(int id) {
		return dao.getRoleById(id);
	}

	@Override
	public void deleteRoleById(int id) {
		dao.deleteRoleById(id);
	}

	@Override
	public Role updateRole(Role role) {
		dao.updateRole(role);
		return role;
	}

	@Override
	public Role addRole(Role role) {
		Role r = new Role();
		boolean success = true;
		
		if(role.getName() != null){
			if(role.getName().length() == 0){
				r.setName("Role name is empty");
				success = false;
			}
		}else{
			r.setName("Role name is empty");
			success = false;
		}
		
		try {
			if(dao.getUserById(role.getUser().getId()) == null){
				r.setName("User not find");
				success = false;
			}
		} catch (Exception e) {
			success = false;
		}
		
		if(success){
			dao.addRole(role);
			return null;
		}else{
			return r;			
		}
	}
}
