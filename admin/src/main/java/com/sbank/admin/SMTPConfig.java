package com.sbank.admin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

/**--------------------------------------------------------------------
 * ■SMTP 환경 설정 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@Configuration
@ConfigurationProperties(prefix="smtp")
public class SMTPConfig {
	private String  host;
	private String  port;
	private Boolean auth;
	private Boolean startTLSEnable;
	private String  user;
	private String  password;

	/**--------------------------------------------------------------------
	 * ■서비스 등록 ■payletter ■2018-09-28
	 --------------------------------------------------------------------**/
	@Bean
	public JavaMailSender getMailSender() {
		JavaMailSenderImpl objJavaMailSenderImpl = new JavaMailSenderImpl();

		objJavaMailSenderImpl.setJavaMailProperties(getMailProperties());

		// 인증 계정이 있는 경우
		if(auth) {
			objJavaMailSenderImpl.setUsername(user);
			objJavaMailSenderImpl.setPassword(password);

			Session session = Session.getInstance(getMailProperties(), new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			});
			session.setDebug(true);
		}

		return objJavaMailSenderImpl;
	}

	/**--------------------------------------------------------------------
	 * ■SMTP 프로퍼티 설정 ■payletter ■2018-09-28
	 --------------------------------------------------------------------**/
	private Properties getMailProperties() {
		Properties properties = new Properties();

		properties.setProperty("mail.transport.protocol",       "smtp");
		properties.setProperty("mail.smtp.starttls.enable",     startTLSEnable ? "true" : "false");
		properties.setProperty("mail.smtp.ssl.trust",           host);
		properties.setProperty("mail.smtp.host",                host);
		properties.setProperty("mail.smtp.auth",                auth ? "true" : "false");
		properties.setProperty("mail.smtp.port",                port);
		properties.setProperty("mail.smtp.socketFactory.port",  port);
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		return properties;
	}
}