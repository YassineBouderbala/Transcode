package com.supinfo.transcode.security;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {
	public static void send(String mess, String sendToMail){
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("supinfo.no.reply.transcode","supinfo123");
				}
			});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("no-reply@transcode.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(sendToMail));
			message.setSubject("File convertion done.");
			message.setText("The file convertion is done. You can download the file now at this address :  "+mess);

			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}		
	}
}
