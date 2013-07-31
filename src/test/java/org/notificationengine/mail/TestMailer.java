package org.notificationengine.mail;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.notificationengine.mail.Mailer;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import static org.junit.Assert.assertTrue;

public class TestMailer {

	private Mailer mailer;
	
	@Before
	public void init() {
		
		SimpleMailMessage templateMessage = new SimpleMailMessage();
		templateMessage.setFrom("**********");
		templateMessage.setSubject("Notification Engine Test Mail");
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("*********");
		mailSender.setPassword("*********");
		
		Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.timeout", "8500");
        mailSender.setJavaMailProperties(properties);
		
		this.mailer = new Mailer();
		
		this.mailer.setMailSender(mailSender);
		
		this.mailer.setTemplateMessage(templateMessage);
	}
	
	@Test
	public void testSendMail() {

        assertTrue(this.mailer.sendMail("mduclos@sqli.com", "Default Test Mail Content."));
	}

}
