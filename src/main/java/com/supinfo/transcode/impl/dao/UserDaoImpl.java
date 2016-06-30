package com.supinfo.transcode.impl.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import com.supinfo.transcode.entity.Role;
import com.supinfo.transcode.entity.User;
import com.supinfo.transcode.interfaces.global.dao.IGlobalUserDao;

public class UserDaoImpl implements IGlobalUserDao {
	
	@PersistenceContext
	EntityManager em;

	@Override
	public User getUserById(int id) {
		try {
			return em.find(User.class, id);			
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public User getUserByUsername(String username) {
		Query req = em.createQuery("SELECT u FROM User u where u.username='"+username+"'");
		return (User) req.getSingleResult();			
	}
	
	@Override
	public User getUserByEmail(String email) {
		Query req = em.createQuery("SELECT u FROM User u where u.email='"+email+"'");
		return (User) req.getSingleResult();			
	}

	@Override
	public void deleteUser(int id) {
		User u = em.find(User.class,id);
		em.remove(u);
	}

	@Override
	public void updateUser(User user) {
		em.merge(user);
	}

	@Override
	public void insertUser(User user) {
		em.persist(user);
	}
	
	/*** ROLE PART ****/

	@Override
	public Role getRoleById(int id) {
		return em.find(Role.class, id);
	}

	@Override
	public void deleteRoleById(int id) {
		Role r = em.find(Role.class, id);
		em.remove(r);
	}

	@Override
	public void updateRole(Role role) {
		em.merge(role);
	}

	@Override
	public void addRole(Role role) {
		em.persist(role);
	}	
}
