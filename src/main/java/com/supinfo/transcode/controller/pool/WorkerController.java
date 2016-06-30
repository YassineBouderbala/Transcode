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
import com.supinfo.transcode.entity.Worker;
import com.supinfo.transcode.interfaces.global.job.IGlobalPoolJob;
import com.supinfo.transcode.interfaces.global.job.IGlobalUserJob;
import com.supinfo.transcode.security.Check;
import com.supinfo.transcode.security.UnauthorizedException;
import com.supinfo.transcode.utils.Response;

import javassist.NotFoundException;

@Controller
public class WorkerController {
	@Autowired
	IGlobalPoolJob job;
	
	@Autowired
	IGlobalUserJob job_user;
	
	private ObjectMapper mapper = null;
	private JsonNode node = null;
	
	@RequestMapping(value="/workers/{pool_id}", method = {RequestMethod.GET})
	public @ResponseBody Response workerG(HttpServletResponse response, HttpServletRequest request, @PathVariable int pool_id){
		Response r = new Response();
		
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			r.setWorkers((List<Worker>) job.getWorkersByPoolId(pool_id));
			r.setErrorCode(200, response);
		
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch (Exception e) {
			r.setErrorCode(500, response);
			return r;
		}
		
		return r;
	}
	
	@RequestMapping(value="/worker/{id_ip}", method = {RequestMethod.GET})
	public @ResponseBody Response poolGG(HttpServletResponse response, HttpServletRequest request,@PathVariable String id_ip){
		Response r = new Response();
		
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			try {
				Worker worker = job.getWorkerById(Integer.parseInt(id_ip));
				if(worker == null){
					r.setErrorCode(404, response);				
				}else{
					r.setWorker(worker);
					r.setErrorCode(200, response);					
				}
				return r;
			} catch (Exception e) {
				Worker worker = job.getWorkerByIp(id_ip);
				if(worker == null){
					r.setErrorCode(404, response);				
				}else{
					r.setWorker(worker);
					r.setErrorCode(200, response);				
				}
				return r;	
			}
		
		}catch (UnauthorizedException exep) {
			r.setErrorCode(500, response);
		}catch (NotFoundException exep) {
			r.setErrorCode(404, response);
		}catch (Exception e) {
			r.setErrorCode(500, response);
		}
		
		return r;
	}
	
	@RequestMapping(value="/worker/{id_pool}", method = {RequestMethod.POST})
	public @ResponseBody Response workerP(HttpServletResponse response, HttpServletRequest request,@RequestBody String json,@PathVariable int id_pool){
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
			
			Worker workerPersist = new Worker();
			workerPersist.getPool().setId(id_pool);
			workerPersist = mapper.convertValue(node.get("worker"), Worker.class);
			
			Worker w = job.addWorker(workerPersist,id_pool);
			
			if(w == null){				
				r.setErrorCode(200,response);
			}else{
				r.setErrorCode(400, response);
				r.setWorker(w);
			}
			
		}catch (NotFoundException ex) {
			r.setErrorCode(404, response);
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch (Exception e) {
			r.setErrorCode(500, response);
			return r;
		}
		
		return r;
	}
	
	@RequestMapping(value="/worker/{id}", method= {RequestMethod.PUT})
	public @ResponseBody Response workerU(HttpServletResponse response, HttpServletRequest request, @PathVariable int id,@RequestBody String json ) throws Exception{
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
			Worker w = job.updateWorker(job.getWorkerById(id), mapper.convertValue(node.get("worker"), Worker.class));
			if(w == null){
				r.setErrorCode(200, response);
			}else{
				r.setWorker(w);
				r.setErrorCode(400, response);
			}
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch(Exception e){
			r.setErrorCode(404, response);
		}
		
		return r;
	}
	
	@RequestMapping(value="/worker/{id}", method = {RequestMethod.DELETE})
	public @ResponseBody Response poolD(HttpServletResponse response, HttpServletRequest request,@PathVariable int id){
		Response r = new Response();
				
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			job.deleteWorker(id);
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
