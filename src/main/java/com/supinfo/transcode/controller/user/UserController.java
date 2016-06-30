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
import com.supinfo.transcode.entity.User;
import com.supinfo.transcode.interfaces.global.job.IGlobalUserJob;
import com.supinfo.transcode.security.Check;
import com.supinfo.transcode.security.Crypter;
import com.supinfo.transcode.security.UnauthorizedException;
import com.supinfo.transcode.utils.FileLink;
import com.supinfo.transcode.utils.Response;

@Controller
public class UserController {
	
	@Autowired
	IGlobalUserJob job;
		
	private ObjectMapper mapper = null;
	private JsonNode node = null;
	
	@RequestMapping(value="/login", method = {RequestMethod.POST})
	public @ResponseBody Response login(HttpServletResponse response, HttpServletRequest request,@RequestBody String json){
		Response r = new Response();
		
		try {
			mapper = new ObjectMapper();
			node = mapper.readTree(json);					
		} catch (Exception e) {
			r.setErrorCode(400, response);
			return r;
		}
		
		try {
			User user = mapper.convertValue(node.get("user"), User.class);
			Check.isLogged(job, user.getUsername(), Crypter.Md5(user.getPassword()));
			user = job.getUserByUsername(user.getUsername());
			user.setFiles(null);
			r.setUser(user);
			r.setErrorCode(200, response);
		}catch (UnauthorizedException ex) {
			r.setErrorCode(404, response);
		} catch (Exception e) {
			r.setErrorCode(500, response);
			return r;
		}
		
		return r;
	}
	
	@RequestMapping(value="/user/{id_username}", method= {RequestMethod.GET})
	public @ResponseBody Response userG(HttpServletResponse response, HttpServletRequest request, @PathVariable String id_username ) throws Exception{
		Response r = new Response();
		try {
			User user = new User();
			Check.isLogged(job, request.getParameter("username"), request.getParameter("password"));
			try {
				user = job.getUserById(Integer.parseInt(id_username));
			} catch (Exception e) {
				user = job.getUserByUsername(id_username);
			}
			if(user != null){
				for (com.supinfo.transcode.entity.File f : user.getFiles()) {
					if(f.getQueue().isFinished() == true){
						f.setDownload_link(new FileLink().getUri()+"/file/download/"+f.getId()+"?username="+f.getUser().getUsername()+"&password="+f.getUser().getPassword());
					}
					f.setUser(null);
				}

				r.setUser(user);
				r.setErrorCode(200, response);
			}else{
				r.setErrorCode(404, response);				
			}
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch(Exception e){
			r.setErrorCode(500, response);
		}
		
		return r;
	}
	
	@RequestMapping(value="/user", method = {RequestMethod.POST})
	public @ResponseBody Response userP(HttpServletResponse response, HttpServletRequest request,@RequestBody String json){
		Response r = new Response();
		
		/* CREATION DE L'UTILISATEUR SCRIPT */
		
		if(job.getUserByUsername("script") == null){
			User u = new User("script", "script", "script", "script@gmail.com", "script", "script");
			job.insertUser(u);
		}
		
		try {
			mapper = new ObjectMapper();
			node = mapper.readTree(json);					
		} catch (Exception e) {
			r.setErrorCode(400, response);
			return r;
		}
		
		try {
			User persistUser = job.insertUser(mapper.convertValue(node.get("user"), User.class));
			
			if(persistUser == null){
				
				r.setErrorCode(200,response);
			}else{
				r.setUser(persistUser);
				r.setErrorCode(401, response);
			}			
		} catch (Exception e) {
			r.setErrorCode(500, response);
			return r;
		}
		
		return r;
	}
	
	@RequestMapping(value="/user/{id}", method= {RequestMethod.DELETE})
	public @ResponseBody Response userD(HttpServletResponse response, HttpServletRequest request, @PathVariable int id ) throws Exception{
		Response r = new Response();
		try {
			Check.isLogged(job, request.getParameter("username"), request.getParameter("password"));
			job.deleteUser(id);
			r.setErrorCode(200, response);
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch(Exception e){
			r.setErrorCode(404, response);
		}
		
		return r;
	}
	
	@RequestMapping(value="/user/{id}", method= {RequestMethod.PUT})
	public @ResponseBody Response userP(HttpServletResponse response, HttpServletRequest request, @PathVariable int id,@RequestBody String json ) throws Exception{
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
			User u = job.updateUser(job.getUserById(id), mapper.convertValue(node.get("user"), User.class));
			if(u == null){
				r.setErrorCode(200, response);
			}else{
				r.setUser(u);
				r.setErrorCode(400, response);
			}
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch(Exception e){
			r.setErrorCode(404, response);
		}
		
		return r;
	}
}
