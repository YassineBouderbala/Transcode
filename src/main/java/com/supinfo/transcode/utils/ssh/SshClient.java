package com.supinfo.transcode.utils.ssh;

public class SshClient {
	
	public static Boolean launch(String argCommand, String ip){
		try {
		     System.out.println("sendCommand");

		     String command = argCommand;
		     String userName = new SshManager().getUsernameSsh();
		     String password = new SshManager().getPasswordSsh();
		     String connectionIP = ip;
		     SshManager instance = new SshManager(userName, password, connectionIP, new SshManager().getFileKeySsh(), 22,120000);
		     String errorMessage = instance.connect();

		     if(errorMessage != null)
		     {
		        System.out.println(errorMessage);
		        return false;
		     }

		     instance.sendCommand(command);

		     instance.close();
			
		     System.out.println("OK");
		     
		     return true;			
		} catch (Exception e) {
			return false;
		}
	}
}
