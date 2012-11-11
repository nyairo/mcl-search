package mcl.search.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import mobitelexx.conf.Config;

public class EMailAction {
	public static final String SUBJECT_NEW_PASSWORD = "Your new password";
	public static final String BODY_NEW_PASSWORD = "Your password is ";
	 	
	
	public static Boolean sendEMail(String receipient, String subject,
			String text) {
			final Config conf = Config.getInstance();
			/*Properties props = new Properties();										
			props.put("mail.transport.protocol", conf.getMAIL_PROTOCOL());					
			props.put("mail.smtp.host", conf.getMAIL_HOST());
			props.put("mail.smtp.user", conf.getMAIL_USER());
			props.put("mail.smtp.password", conf.getMAIL_USER_PASSWORD());
			props.put("mail.smtp.port", conf.getMAIL_PORT());
			props.put("mail.smtp.auth", conf.getMAIL_USE_AUTH());
			props.put("mail.smtp.starttls.enable", conf.getMAIL_USE_SSL());

			Session s = Session.getInstance(props, null);
//					s.setDebug(true);
			
			MimeMessage message = new MimeMessage(s);
			try {
				//InternetAddress from = new InternetAddress("edwin.nyairo@verveko.com");
				InternetAddress from = new InternetAddress(conf.getMAIL_FROM_ADDRESS());
				message.setFrom(from);

				InternetAddress to = new InternetAddress(receipient);
				message.addRecipient(Message.RecipientType.TO, to);
				message.setSubject(subject);
				message.setText(text);
				
				Transport.send(message);
				return true;
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			*/
					
			Properties props = new Properties();
			props.put("mail.smtp.host", conf.getMAIL_HOST());
			props.put("mail.smtp.socketFactory.port", conf.getMAIL_PORT());
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", conf.getMAIL_USE_AUTH());
			props.put("mail.smtp.port", conf.getMAIL_PORT());
	 
			Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(conf.getMAIL_USER(),conf.getMAIL_USER_PASSWORD());
					}
				});
	 
			try {
	 
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(conf.getMAIL_FROM_ADDRESS()));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(receipient));
				message.setSubject(subject);
				message.setText(text);
	 
				Transport.send(message);
	 
				return true;
			} catch (MessagingException e) {
				return false;	
			}	
		}
		
}
/*
public void sendEmail(String username, String password) throws EmailException {

MultiPartEmail email = new MultiPartEmail();
email.setHostName("mail.test.com");

		EmailAttachment logFile = new EmailAttachment();
		logFile.setPath("C:\\test.log");
		logFile.setDisposition(EmailAttachment.ATTACHMENT);
		logFile.setDescription("Log-File");


		generateTo(email, toText.getText());
		if (!ccText.getText().equals("")) {
			email.addCc(ccText.getText());
		}
		email.setFrom("plu@xxxxxxxx", "Peter Lustig");
		email.setSubject(subjectText.getText());
		email.setMsg(messageText.getText());
		if (attachLogfileButton.getSelection()) {
			email.attach(logFile);
		}
		email.setAuthentication(username, password);
		email.send();
	}*/
/*	private static Boolean sendEMail(String recipient, String subject,
		String text) {
				Properties props = new Properties();
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.host", "smtp.yourhost");
				props.put("mail.smtp.user", "user");

				Session s = Session.getInstance(props, null);
//				s.setDebug(true);
				
				MimeMessage message = new MimeMessage(s);
				try {
					InternetAddress from = new InternetAddress("reply@xxxxxxxxx");
					message.setFrom(from);

					InternetAddress to = new InternetAddress(recipient);
					message.addRecipient(Message.RecipientType.TO, to);
					message.setSubject(subject);
					message.setText(text);
					
					Transport.send(message);
					return true;
				} catch (AddressException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				return false;
			}

*/