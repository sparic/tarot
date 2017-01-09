package com.myee.tarot.core.util;

import com.myee.tarot.core.Constants;
import com.myee.tarot.core.util.ajax.AjaxResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import java.util.Properties;

@Component("sendemail")
public class EmailSenderUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderUtil.class);

	private JavaMailSenderImpl email;

    private SimpleMailMessage message;

	public EmailSenderUtil() {
		// TODO Auto-generated constructor stub
		email = new JavaMailSenderImpl();
		email.setHost(Constants.MAIL_HOST);
		email.setUsername(Constants.MAIL_SENDER_ACCOUNT);
		email.setPassword(Constants.MAIL_SENDER_PSW);//授权码
		email.setPort(Constants.MAIL_HOST_PORT);
		Properties properties = new Properties();
		properties.put(Constants.MAIL_AUTH, true);
		properties.put(Constants.MAIL_SSL, true);
		properties.put(Constants.MAIL_SOCKET_CLASS, Constants.MAIL_CREATE_FACTORY);
		properties.put(Constants.MAIL_TIMEOUT_CLASS, Constants.MAIL_TIMEOUT_MILE_SECONDS);
		email.setJavaMailProperties(properties);
	}

	//发送邮件
	public AjaxResponse send(String from, String[] to, String subject, String context){
		try {
			message = new SimpleMailMessage();
			message.setFrom(from);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(context);
			email.send(message);
			return AjaxResponse.success("发送成功!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResponse.failed(-1, "发送失败!");
		}
	}

	public static void main(String[] args) {
		EmailSenderUtil instance = new EmailSenderUtil();
		String[] a = new String[1];
		a[0] = "jelynn.tang@mrobot.cn";
		instance.send("cloudmanager@mrobot.cn", a, "test", "test");
	}

}