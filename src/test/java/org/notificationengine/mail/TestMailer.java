package org.notificationengine.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.notificationengine.constants.Constants;
import org.notificationengine.mail.Mailer;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import static org.junit.Assert.assertTrue;

public class TestMailer {

	private Mailer mailer;
	
	@Before
	public void init() {
		
		SimpleMailMessage templateMessage = new SimpleMailMessage();
		templateMessage.setFrom("mduclos@sqli.com");
		templateMessage.setSubject("Notification Engine Test Mail");
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("mduclos@sqli.com");
		mailSender.setPassword("********");
		
		Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.timeout", "8500");
        properties.setProperty("mail.from", "mduclos@sqli.com");
        mailSender.setJavaMailProperties(properties);
		
		this.mailer = new Mailer();
		
		this.mailer.setMailSender(mailSender);

        this.mailer.setLocalSettingsProperties(properties);
		
		this.mailer.setTemplateMessage(templateMessage);
	}
	
	@Test
	public void testSendMail() {

        assertTrue(this.mailer.sendMail("mduclos@sqli.com", "Default Test Mail Content.", Boolean.FALSE, null, null));
	}

    @Test
    public void testSendMailWithSubject() {

    	Map<String, String> options = new HashMap<>();
    	options.put(Constants.SUBJECT, "NotificationEngine Test with custom subject");
    	
        assertTrue(this.mailer.sendMail("mduclos@sqli.com", "Mail with a different subject", Boolean.FALSE, null, options));
    }

    @Test
    public void testSendMailWithSubjectAndFromField() {

    	Map<String, String> options = new HashMap<>();
    	options.put(Constants.SUBJECT, "NotificationEngine Test with custom subject and from");
    	options.put(Constants.FROM, "matthis.duclos@gmail.com");

        assertTrue(this.mailer.sendMail("mduclos@sqli.com",
                "Mail with a different subject and from field",
                Boolean.FALSE,
                null,
                options));
    }

}
