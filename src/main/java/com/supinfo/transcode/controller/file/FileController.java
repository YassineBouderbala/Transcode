package com.supinfo.transcode.controller.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletContext;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.supinfo.transcode.entity.File;
import com.supinfo.transcode.entity.File_part;
import com.supinfo.transcode.entity.Pool;
import com.supinfo.transcode.entity.Streaming;
import com.supinfo.transcode.interfaces.global.job.IGlobalFileJob;
import com.supinfo.transcode.interfaces.global.job.IGlobalPoolJob;
import com.supinfo.transcode.interfaces.global.job.IGlobalUserJob;
import com.supinfo.transcode.security.Check;
import com.supinfo.transcode.security.UnauthorizedException;
import com.supinfo.transcode.utils.FileLink;
import com.supinfo.transcode.utils.Response;
import com.supinfo.transcode.utils.ssh.ThreadSsh;

import javassist.NotFoundException;

@Controller
public class FileController {
	
	@Autowired
	IGlobalFileJob job;
		
	@Autowired
	IGlobalPoolJob job_pool;
	
	@Autowired
	IGlobalUserJob job_user;
		
    @Autowired
    ServletContext context; 
	
	private ObjectMapper mapper = null;
	private JsonNode node = null;
		
	@RequestMapping(value="/file/download/{id}", method = {RequestMethod.GET})	
	public void download(HttpServletResponse response, HttpServletRequest request,@PathVariable int id) throws IOException{
		Response r = new Response();
				
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			
			java.io.File file = new java.io.File(new FileLink().getLinkFileDownload()+"/"+id+"/");
			String names[] = file.list();
			
			for (String name : names) {
				if(name.charAt(0) != '.'){
					names[0] = name;
				}
			}
			
			String filePath = new FileLink().getLinkFileDownload()+"/"+id+"/"+names[0];
			
			java.io.File downloadFile = new java.io.File(filePath);
			FileInputStream inStream = new FileInputStream(downloadFile);

			String mimeType = context.getMimeType(filePath);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			System.out.println("MIME type: " + mimeType);

			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[4096];
			int bytesRead = -1;

			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inStream.close();
			outStream.close();			
		}catch (Exception e) {
			response.setCharacterEncoding("utf-8");
			response.getWriter().println("Fichier manquant ou non autorisé");
		}
	}
	
	@RequestMapping(value="/file/{id}", method = {RequestMethod.GET})	
	public @ResponseBody Response fileGet(HttpServletResponse response, HttpServletRequest request,@PathVariable int id){
		Response r = new Response();
				
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			
			
			File f = job.getFileById(id);
			
			if(f == null){				
				r.setErrorCode(404,response);
			}else{
				r.setErrorCode(200, response);
				if(f.getQueue().isFinished() == true){
					f.setDownload_link(new FileLink().getUri()+"/file/download/"+f.getId()+"?username="+f.getUser().getUsername()+"&password="+f.getUser().getPassword());
				}
				f.getUser().setFiles(null);
				f.setUser_file(null);
				r.setFile(f);
			}
		
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch (Exception e) {
			r.setErrorCode(500, response);
			return r;
		}
		
		return r;
	}
	
	@RequestMapping(value="/file/{convertType}", method = {RequestMethod.POST})	
	public @ResponseBody Response fileG(@RequestParam("file") MultipartFile file,HttpServletResponse response, HttpServletRequest request,@PathVariable String convertType){
		Response r = new Response();
				
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			
			File persistFile = new File();
			persistFile.setConvertValue(convertType);
			
			File f = job.addFile(request.getParameter("username"),persistFile,file);
			
			if(f == null){				
				r.setErrorCode(200,response);
			}else{
				r.setErrorCode(400, response);
				r.setFile(f);
			}
		
		} catch (UnauthorizedException ex) {
			r.setErrorCode(401, response);
		}catch (Exception e) {
			r.setErrorCode(500, response);
			return r;
		}
		
		return r;
	}
	
	@RequestMapping(value="/file/queue/{id}", method = {RequestMethod.PUT})	
	public @ResponseBody Response fileM(HttpServletResponse response, HttpServletRequest request,@PathVariable int id,@RequestBody String json){
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
				File f = job.updateFile(job.getFileById(id), mapper.convertValue(node.get("file"), File.class));
				if(f == null){
					r.setErrorCode(200, response);
				}else{
					r.setErrorCode(400, response);
				}
			} catch (UnauthorizedException ex) {
				r.setErrorCode(401, response);
			}catch(Exception e){
				r.setErrorCode(404, response);
			}
				
		return r;
	}
	
	
	/***** PART *****/
	
	@RequestMapping(value="/file/part/{id}", method = {RequestMethod.POST})	
	public @ResponseBody Response file_part_post(HttpServletResponse response, HttpServletRequest request,@PathVariable int id,@RequestBody String json){
		Response r = new Response();
							
			try {
				mapper = new ObjectMapper();
				node = mapper.readTree(json);					
			} catch (Exception e) {
				r.setErrorCode(400, response);
				return r;
			}
			Integer numberFile = mapper.convertValue(node.get("numberFile"), Integer.class);
			try {
				Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
				job.addFile_Part(job.getFileById(id), numberFile);
				r.setErrorCode(200, response);
			} catch (UnauthorizedException ex) {
				r.setErrorCode(401, response);
			}catch(Exception e){
				r.setErrorCode(404, response);
			}
				
		return r;
	}
	
	@RequestMapping(value="/file/part/{id_file}", method = {RequestMethod.PUT})	
	public @ResponseBody Response file_part_put(HttpServletResponse response, HttpServletRequest request,@PathVariable int id_file,@RequestBody String json){
		Response r = new Response();
							
			try {
				mapper = new ObjectMapper();
				node = mapper.readTree(json);					
			} catch (Exception e) {
				r.setErrorCode(400, response);
				return r;
			}
			File_part file_part = mapper.convertValue(node.get("file"), File_part.class);
			try {
				Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
				
				File_part f = new File_part();
				f = job.getFilePartByNameFileId(file_part.getName(), id_file);
				
				if(f == null || job.getFileById(id_file) == null){
					throw new Exception("Not found");
				}
				
				job.updateFile_Part(f, file_part);
				r.setErrorCode(200, response);
			} catch (UnauthorizedException ex) {
				r.setErrorCode(401, response);
			}catch(Exception e){
				r.setErrorCode(404, response);
			}
				
		return r;
	}
	
}
