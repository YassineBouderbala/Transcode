package com.supinfo.transcode.controller.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.supinfo.transcode.entity.Role;
import com.supinfo.transcode.entity.User;
import com.supinfo.transcode.interfaces.global.job.IGlobalUserJob;
import com.supinfo.transcode.security.Check;
import com.supinfo.transcode.security.UnauthorizedException;
import com.supinfo.transcode.utils.Response;

@Controller
public class RoleController {
	@Autowired
	IGlobalUserJob job;
	
	private ObjectMapper mapper = null;
	private JsonNode node = null;
	
	@RequestMapping(value="/role/{id_user}", method = {RequestMethod.POST})
	public @ResponseBody Response roleP(HttpServletResponse response, HttpServletRequest request,@RequestBody String json,@PathVariable int id_user){
		Response r = new Response();
		
		try {
			mapper = new ObjectMapper();
			node = mapper.readTree(json);					
		} catch (Exception e) {
			r.setErrorCode(400, response);
			return r;
		}
		
		try {
			Check.isLogged(job, request.getParameter("username"), request.getParameter("password"));
			Role persistRole = new Role();
			persistRole = mapper.convertValue(node.get("role"), Role.class);
			persistRole.getUser().setId(id_user);
			Role role = job.addRole(persistRole);
			
			if(role == null){
				
				r.setErrorCode(200,response);
			}else{
				r.setRole(role);
				r.setErrorCode(401, response);
			}			
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch (Exception e) {
			r.setErrorCode(500, response);
			return r;
		}
		
		return r;
	}
	
	@RequestMapping(value="/role/{id}", method = {RequestMethod.DELETE})
	public @ResponseBody Response roleD(HttpServletResponse response, HttpServletRequest request,@PathVariable int id){
		Response r = new Response();
				
		try {
			Check.isLogged(job, request.getParameter("username"), request.getParameter("password"));
			job.deleteRoleById(id);
			r.setErrorCode(200,response);		
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch (Exception e) {
			r.setErrorCode(404, response);
			return r;
		}
		
		return r;
	}
}
