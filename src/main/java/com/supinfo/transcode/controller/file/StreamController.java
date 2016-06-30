package com.supinfo.transcode.controller.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
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
public class StreamController {
	
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
	
	@RequestMapping(value="/stream/insert", method = {RequestMethod.POST})	
	public @ResponseBody Response download(HttpServletResponse response, HttpServletRequest request,@RequestBody String json) throws IOException{
		Response r = new Response();
				
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			
			try {
				mapper = new ObjectMapper();
				node = mapper.readTree(json);					
			} catch (Exception e) {
				r.setErrorCode(400, response);
				return r;
			}
			String link = mapper.convertValue(node.get("link"), String.class);
			
			Streaming stream = new Streaming();
			stream.setFinished(false);
			stream.setName(link);
			stream.setUser(job_user.getUserByUsername(request.getParameter("username")));			
			job.insertStream(stream);
						
        	new ThreadSsh("screen -dm youtube-dl --no-check-certificate -o '"+new FileLink().getLinkFileUploadStream()+"/"+job.getLastIdStreaming()+"/%(title)s' -f 22 '"+link+"' ").run();
			
			r.setErrorCode(200, response);
			r.setStream(stream);
		}catch (Exception e) {
			r.setErrorCode(401, response);
		}
		return r;	
	}
	
	@RequestMapping(value="/streams", method = {RequestMethod.GET})	
	public @ResponseBody Response getStream(HttpServletResponse response, HttpServletRequest request,@RequestBody String json) throws IOException{
		Response r = new Response();
				
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			
			List<Streaming> streams = (List<Streaming>) job.getAllStreamByUser(request.getParameter("username"));
			
			for (Streaming s : streams) {
				try {
					java.io.File file = new java.io.File(new FileLink().getLinkFileUploadStream()+"/"+s.getId()+"/");
					String names[] = file.list();
					
					for (String name : names) {
						if(name.charAt(0) != '.'){
							names[0] = name;
						}
					}
					
					String filePath = new FileLink().getLinkFileUploadStream()+"/"+s.getId()+"/"+names[0];
					
					java.io.File downloadFile = new java.io.File(filePath);	
											
					if(FilenameUtils.getExtension(downloadFile.getPath()).equals("part")){
						s.setFinished(false);
					}else{
						s.setFinished(true);
						s.setLink(new FileLink().getUri()+"/stream/download/"+s.getId()+"?username="+request.getParameter("username")+"&password="+request.getParameter("password"));					
					}					
				} catch (Exception e) {
					s.setFinished(false);
				}
			}

			r.setStreams(streams);
			r.setErrorCode(200, response);
		}catch (Exception e) {
			r.setErrorCode(401, response);
		}
		return r;	
	}
	
	@RequestMapping(value="/stream/download/{id}", method = {RequestMethod.GET})	
	public void download(HttpServletResponse response, HttpServletRequest request,@PathVariable int id) throws IOException{
		Response r = new Response();
				
		try {
			Check.isLogged(job_user, request.getParameter("username"), request.getParameter("password"));
			
			java.io.File file = new java.io.File(new FileLink().getLinkFileUploadStream()+"/"+id+"/");
			String names[] = file.list();
			
			for (String name : names) {
				if(name.charAt(0) != '.'){
					names[0] = name;
				}
			}
			
			String filePath = new FileLink().getLinkFileUploadStream()+"/"+id+"/"+names[0];
			
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
	
}
