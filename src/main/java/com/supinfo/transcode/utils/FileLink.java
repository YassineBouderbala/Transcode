package com.supinfo.transcode.utils;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FileLink {	
	private JSONParser parser = new JSONParser();
	private InputStream jsonFileConfig = this.getClass().getClassLoader().getResourceAsStream("META-INF/config.json");
	
	public String getLinkFileUpload(){
		try {
			Object obj = parser.parse(IOUtils.toString(jsonFileConfig, "UTF-8"));
			JSONObject jsonObject = (JSONObject) obj;
			return (String) jsonObject.get("link_upload");
		}catch (Exception e) {
			return null;
		}
	}
	
	public String getLinkFileUploadStream(){
		try {
			Object obj = parser.parse(IOUtils.toString(jsonFileConfig, "UTF-8"));
			JSONObject jsonObject = (JSONObject) obj;
			return (String) jsonObject.get("link_download_stream");
		}catch (Exception e) {
			return null;
		}
	}
	
	public String getLinkFileDownload(){
		try {
			Object obj = parser.parse(IOUtils.toString(jsonFileConfig, "UTF-8"));
			JSONObject jsonObject = (JSONObject) obj;
			return (String) jsonObject.get("link_download");
		}catch (Exception e) {
			return null;
		}
	}
	
	public String getUri(){
		try {
			Object obj = parser.parse(IOUtils.toString(jsonFileConfig, "UTF-8"));
			JSONObject jsonObject = (JSONObject) obj;
			return (String) jsonObject.get("uri");
		}catch (Exception e) {
			return null;
		}
	}
}
