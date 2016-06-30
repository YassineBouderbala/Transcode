package com.supinfo.transcode.controller.pool;

import java.util.List;

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

import com.supinfo.transcode.entity.Pool;
import com.supinfo.transcode.entity.Role;
import com.supinfo.transcode.entity.User;
import com.supinfo.transcode.interfaces.global.job.IGlobalPoolJob;
import com.supinfo.transcode.interfaces.global.job.IGlobalUserJob;
import com.supinfo.transcode.security.Check;
import com.supinfo.transcode.security.UnauthorizedException;
import com.supinfo.transcode.utils.Response;

import javassist.NotFoundException;

@Controller
public class PoolController {
	@Autowired
	IGlobalPoolJob job;
	
	@Autowired
	IGlobalUserJob job_user;
	
	private ObjectMapper mapper = null;
	private JsonNode node = null;
	
	@RequestMapping(value="/pools", method = {RequestMethod.GET})
	public @ResponseBody Response poolG(HttpServletResponse response, HttpServletRequest request){
		Response r = new Response();
		
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			r.setPools((List<Pool>) job.getPools());
			r.setErrorCode(200, response);
		
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch (Exception e) {
			r.setErrorCode(500, response);
			return r;
		}
		
		return r;
	}
	
	@RequestMapping(value="/pool/{id}", method = {RequestMethod.GET})
	public @ResponseBody Response poolGG(HttpServletResponse response, HttpServletRequest request,@PathVariable int id){
		Response r = new Response();
		
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			r.setPool(job.getPoolById(id));
			r.setErrorCode(200, response);
		
		}catch (UnauthorizedException exep) {
			r.setErrorCode(500, response);
		}catch (NotFoundException exep) {
			r.setErrorCode(404, response);
		}catch (Exception e) {
			r.setErrorCode(500, response);
		}
		
		return r;
	}
	
	@RequestMapping(value="/pool", method = {RequestMethod.POST})
	public @ResponseBody Response poolP(HttpServletResponse response, HttpServletRequest request,@RequestBody String json){
		Response r = new Response();
		
		try {
			mapper = new ObjectMapper();
			node = mapper.readTree(json);					
		} catch (Exception e) {
			r.setErrorCode(400, response);
			return r;
		}
		
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			
			Pool persistPool = new Pool();
			persistPool = mapper.convertValue(node.get("pool"), Pool.class);
			
			Pool pool = job.insertPool(persistPool);
			
			if(pool == null){				
				r.setErrorCode(200,response);
				r.setPool(pool);
			}else{
				r.setErrorCode(400, response);
				r.setPool(pool);
			}
			
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch (Exception e) {
			r.setErrorCode(500, response);
			return r;
		}
		
		return r;
	}
	
	@RequestMapping(value="/pool/{id}", method= {RequestMethod.PUT})
	public @ResponseBody Response poolU(HttpServletResponse response, HttpServletRequest request, @PathVariable int id,@RequestBody String json ) throws Exception{
		Response r = new Response();
		
		try {
			mapper = new ObjectMapper();
			node = mapper.readTree(json);					
		} catch (Exception e) {
			r.setErrorCode(400, response);
			return r;
		}
		
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			Pool p = job.updatePool(job.getPoolById(id), mapper.convertValue(node.get("pool"), Pool.class));
			if(p == null){
				r.setErrorCode(200, response);
			}else{
				r.setPool(p);
				r.setErrorCode(400, response);
			}
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch(Exception e){
			r.setErrorCode(404, response);
		}
		
		return r;
	}
	
	@RequestMapping(value="/pool/{id}", method = {RequestMethod.DELETE})
	public @ResponseBody Response poolD(HttpServletResponse response, HttpServletRequest request,@PathVariable int id){
		Response r = new Response();
				
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			job.deletePool(id);
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
