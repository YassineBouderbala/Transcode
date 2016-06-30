package com.supinfo.transcode.security;

import com.supinfo.transcode.entity.User;
import com.supinfo.transcode.interfaces.global.job.IGlobalUserJob;

public class Check {
	public static void isLogged(IGlobalUserJob job, String username, String password) throws Exception{
		try {
			User u = job.getUserByUsername(username);
			if(!password.equals(u.getPassword())){
				throw new UnauthorizedException("Unauthorized");
			}			
		} catch (Exception e) {
			throw new UnauthorizedException("Unauthorized");
		}
	}
}
