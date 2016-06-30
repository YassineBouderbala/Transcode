package com.supinfo.transcode.utils.ssh;

public class ThreadSsh implements Runnable {
	
	private String command = null;
	
	public ThreadSsh(String command) {
		this.command = command;
	}
	
	@Override
	public void run() {
		System.out.println("Lancement du Thread");
		SshClient.launch(this.command, "127.0.0.1");		
	}

}
