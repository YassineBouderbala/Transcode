package com.supinfo.transcode.utils;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.supinfo.transcode.entity.File;
import com.supinfo.transcode.entity.Pool;
import com.supinfo.transcode.entity.Role;
import com.supinfo.transcode.entity.Streaming;
import com.supinfo.transcode.entity.User;
import com.supinfo.transcode.entity.Worker;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Response {

	private int errorCode;
	private String description;
	private boolean success;
	
	/** SIMPLE OBJECT **/
	
	private User user;
	private Role role;
	private Pool pool;
	private Worker worker;
	private File file;
	private Streaming stream;
	
	/** LIST OBJECTS **/
	
	private List<Pool> pools;
	private List<Worker> workers;
	private List<Streaming> streams;

	public void setErrorCode(int errorCode, HttpServletResponse res){
		res.setStatus(errorCode);
		this.setErrorCode(errorCode);
	}

	private void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
		
		switch(this.errorCode){
		case 200:
			this.setDescription("OK");
			this.setSuccess(true);
			break;
		case 400:
			this.setDescription("Bad Request");
			this.setSuccess(false);
			break;
		case 401:
			this.setDescription("Unauthorized");
			this.setSuccess(false);
			break;
		case 403:
			this.setDescription("Forbidden");
			this.setSuccess(false);
			break;
		case 404:
			this.setDescription("Not Found");
			this.setSuccess(false);
			break;
		case 405:
			this.setDescription("Method Not Allowed");
			this.setSuccess(false);
			break;
		case 500:
			this.setDescription("Unknown error");
			this.setSuccess(false);
			break;
		default:
			this.setDescription(null);
			this.setSuccess(false);
			break;
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Pool getPool() {
		return pool;
	}

	public void setPool(Pool pool) {
		this.pool = pool;
	}

	public List<Pool> getPools() {
		return pools;
	}

	public void setPools(List<Pool> pools) {
		this.pools = pools;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public List<Worker> getWorkers() {
		return workers;
	}

	public void setWorkers(List<Worker> workers) {
		this.workers = workers;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Streaming getStream() {
		return stream;
	}

	public void setStream(Streaming stream) {
		this.stream = stream;
	}

	public List<Streaming> getStreams() {
		return streams;
	}

	public void setStreams(List<Streaming> streams) {
		this.streams = streams;
	}

}
