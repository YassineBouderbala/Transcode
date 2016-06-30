package com.supinfo.transcode.utils.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshManager {
	private JSONParser parser = new JSONParser();
	private InputStream jsonFileConfig = this.getClass().getClassLoader().getResourceAsStream("META-INF/config_ssh.json");
	private static final Logger LOGGER = Logger.getLogger(SshManager.class.getName());
	private JSch jschSSHChannel;
	private String strUserName;
	private String strConnectionIP;
	private int intConnectionPort;
	private String strPassword;
	private Session sesConnection;
	private int intTimeOut;

	private void doCommonConstructorActions(String userName, String password, String connectionIP,
			String knownHostsFileName) {
		jschSSHChannel = new JSch();

		try {
			jschSSHChannel.setKnownHosts(knownHostsFileName);
		} catch (JSchException jschX) {
			logError(jschX.getMessage());
		}

		strUserName = userName;
		strPassword = password;
		strConnectionIP = connectionIP;
	}
	
	public SshManager(){
		
	}

	public SshManager(String userName, String password, String connectionIP, String knownHostsFileName) {
		doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName);
		intConnectionPort = 22;
		intTimeOut = 60000;
	}

	public SshManager(String userName, String password, String connectionIP, String knownHostsFileName,
			int connectionPort) {
		doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName);
		intConnectionPort = connectionPort;
		intTimeOut = 60000;
	}

	public SshManager(String userName, String password, String connectionIP, String knownHostsFileName,
			int connectionPort, int timeOutMilliseconds) {
		doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName);
		intConnectionPort = connectionPort;
		intTimeOut = timeOutMilliseconds;
	}

	public String connect() {
		String errorMessage = null;

		try {
			sesConnection = jschSSHChannel.getSession(strUserName, strConnectionIP, intConnectionPort);
			sesConnection.setPassword(strPassword);
			sesConnection.connect(intTimeOut);
		} catch (JSchException jschX) {
			errorMessage = jschX.getMessage();
		}

		return errorMessage;
	}

	private String logError(String errorMessage) {
		if (errorMessage != null) {
			LOGGER.log(Level.SEVERE, "{0}:{1} - {2}",
					new Object[] { strConnectionIP, intConnectionPort, errorMessage });
		}

		return errorMessage;
	}

	private String logWarning(String warnMessage) {
		if (warnMessage != null) {
			LOGGER.log(Level.WARNING, "{0}:{1} - {2}",
					new Object[] { strConnectionIP, intConnectionPort, warnMessage });
		}

		return warnMessage;
	}

	public String sendCommand(String command) {
		StringBuilder outputBuffer = new StringBuilder();

		try {
			Channel channel = sesConnection.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			InputStream commandOutput = channel.getInputStream();
			channel.connect();
			int readByte = commandOutput.read();

			/*outputBuffer.append((char) readByte);
			readByte = commandOutput.read();*/

			channel.disconnect();
		} catch (IOException ioX) {
			logWarning(ioX.getMessage());
			return null;
		} catch (JSchException jschX) {
			logWarning(jschX.getMessage());
			return null;
		}

		return outputBuffer.toString();
	}

	public void close() {
		sesConnection.disconnect();
	}

	/******** SSH ********/
	
	public String getUsernameSsh() {
		try {
			Object obj = parser.parse(IOUtils.toString(jsonFileConfig, "UTF-8"));
			JSONObject jsonObject = (JSONObject) obj;
			return (String) jsonObject.get("username");
		}catch (Exception e) {
			return null;
		}
	}
	
	public String getPasswordSsh() {
		try {
			Object obj = parser.parse(IOUtils.toString(jsonFileConfig, "UTF-8"));
			JSONObject jsonObject = (JSONObject) obj;
			return (String) jsonObject.get("password");
		}catch (Exception e) {
			return null;
		}
	}
	
	public String getFileKeySsh() {
		try {
			Object obj = parser.parse(IOUtils.toString(jsonFileConfig, "UTF-8"));
			JSONObject jsonObject = (JSONObject) obj;
			return (String) jsonObject.get("file_key");
		}catch (Exception e) {
			return null;
		}
	}
}
