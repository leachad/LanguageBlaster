/**
 * 
 */
package model;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author aleach
 *
 */
public class EmailTest {

	/**Private field to hold the current recipient.*/
	private String myRecipient;

	/**Private field to hold the byte array.*/
	private byte[] myAttachment;

	/**Private field to hold the current school.*/
	private String mySchool;
	
	/**Private field to hold the current month.*/
	private String myCurrentMonth;



	public EmailTest(final String theRecipient, 
			final byte[] theAttachment,
			final String theSchool,
			final String theMonth) {

		myRecipient = theRecipient;

		myAttachment = theAttachment;

		mySchool = theSchool;
		
		myCurrentMonth = theMonth;

		start();

	}

	private class SMTPAuthenticator extends Authenticator

	{

		private PasswordAuthentication authentication;


		public SMTPAuthenticator(String login, String password) {

			authentication = new PasswordAuthentication(login, password);

		}


		@Override
		protected PasswordAuthentication getPasswordAuthentication() {

			return authentication;
		}

	}


	private void start() {
		
		String USER_NAME = "aleach";  // GMail user name (just the part before "@gmail.com")
		String PASSWORD = "Scrulipee32"; // GMail password
		String RECIPIENT = myRecipient;


		String from = USER_NAME;
		String pass = PASSWORD;
		String to = RECIPIENT; // list of recipient email addresses
		String subject = myCurrentMonth + " Counts";
		String body = "Attached you will find this months count. /n --Andy";

		sendFromGMail(from, pass, to, subject, body);
	}




	private void sendFromGMail(String from, String pass, String to, String subject, String body) {

		Properties props = new Properties();
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.debug", "true");
		
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.port", "587");
		//props.setProperty("mail.smtp.auth.mechanisms", "NTLM");
		props.setProperty("mail.smtp.starttls.enable", "true");
		
		//props.setProperty("mail.smtp.auth.ntlm.domain", "WINDOMAIN");

		Authenticator guard = new SMTPAuthenticator(from, pass);

		// Session session = Session.getDefaultInstance(props, guard);
		Session session = Session.getInstance(props, guard);

		MimeMessage message = new MimeMessage(session);

		try {

			Transport transport = session.getTransport("smtp");
			
			transport.connect("EXCHMBX1.TPS-AD.LOCAL", from, pass);
			
			message.setSubject(subject);
			message.setText(body);

			message.setFrom(new InternetAddress(from));
			
			Multipart mp = new MimeMultipart();

			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setFileName(mySchool.concat(".xls"));
			mbp.setContent(myAttachment, "application/vnd.ms-excel");

			mp.addBodyPart(mbp);
			
			message.setContent(mp);
			
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(to));
			Transport.send(message);
			transport.close();
			
		} catch (MessagingException ex) {
			 
              Logger.getLogger(EmailTest.class.getName()).
              log(Level.SEVERE, null, ex);
              
         }
	}
	

}
