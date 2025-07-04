package com.kerneldc.fls.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.service.http.HttpRequestTypeEnum;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

	@Value("${application.email.emailNotificationFrom}")
	private String emailNotificationFrom;
	@Value("${application.email.emailNotificationTo}")
	private String metarJobNotificationTo;
	
	private static final String REFRESH_AIRPORT_IDENTIFIERS_FAILURE_SUBJECT = "Refresh Airport Identifiers Failure";
	private static final String RERMOTE_API_FAILURE_SUBJECT = "Remote API Failure";
	private static final String RERMOTE_API_SUCCESS_AFTER_RETRY_SUBJECT = "Remote API Success After Retry";
	private static final String REFRESH_AIRPORT_IDENTIFIERS_FAILURE_TEMPLATE = "refreshAirportIdentifiersFailure.ftlh";
	private static final String REMOTE_API_FAILURE_TEMPLATE = "remoteApiFailure.ftlh";
	private static final String REMOTE_API_SUCCESS_AFTER_FAILURE_TEMPLATE = "remoteApiSuccessAfterRetry.ftlh";
	private JavaMailSender javaMailSender;
	private Configuration freeMarkerConfiguration;
	
	public EmailService(JavaMailSender emailSender, Configuration freeMarkerConfiguration) {
		this.javaMailSender = emailSender;
		this.freeMarkerConfiguration = freeMarkerConfiguration;
	}
	
	public void sendRefreshIdentifiersFailureEmail(ApplicationException loadingFromExternalApiException) {
		var mimeMessage = javaMailSender.createMimeMessage();
		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
		try {
			mimeMessageHelper.setFrom(emailNotificationFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(metarJobNotificationTo));
			mimeMessageHelper.setSubject(REFRESH_AIRPORT_IDENTIFIERS_FAILURE_SUBJECT);
			mimeMessageHelper.setText(processRefreshIdentifiersFailureTemplate(loadingFromExternalApiException), true);
			javaMailSender.send(mimeMessage);
			LOGGER.info("Sent refresh identifiers failure email to: {}", metarJobNotificationTo);
		} catch (MessagingException | IOException | TemplateException e) {
			var message = "Exception while sending failure email."; 
			LOGGER.error(message, e);
			LOGGER.info("Failed to send refresh identifiers failure email to: {}", metarJobNotificationTo);
		}
	}
	public void sendRemoteApiFailureEmail(HttpRequestTypeEnum httpRequestTypeEnum, ApplicationException loadingFromExternalApiException) {
		var mimeMessage = javaMailSender.createMimeMessage();
		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
		try {
			mimeMessageHelper.setFrom(emailNotificationFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(metarJobNotificationTo));
			mimeMessageHelper.setSubject(RERMOTE_API_FAILURE_SUBJECT);
			mimeMessageHelper.setText(processRemoteApiFailureTemplate(httpRequestTypeEnum, loadingFromExternalApiException), true);
			javaMailSender.send(mimeMessage);
			LOGGER.info("Sent remote api failure email to: {}", metarJobNotificationTo);
		} catch (MessagingException | IOException | TemplateException e) {
			var message = "Exception while sending failure email."; 
			LOGGER.error(message, e);
			LOGGER.info("Failed to send remote api failure email to: {}", metarJobNotificationTo);
		}
	}
	public void sendRemoteApiSuccessAfterRetryEmail(HttpRequestTypeEnum httpRequestTypeEnum, int retryCount) {
		var mimeMessage = javaMailSender.createMimeMessage();
		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
		try {
			mimeMessageHelper.setFrom(emailNotificationFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(metarJobNotificationTo));
			mimeMessageHelper.setSubject(RERMOTE_API_SUCCESS_AFTER_RETRY_SUBJECT);
			mimeMessageHelper.setText(processRemoteApiSuccessAfterRetryTemplate(httpRequestTypeEnum, retryCount), true);
			javaMailSender.send(mimeMessage);
			LOGGER.info("Sent reomte api success after retry email to: {}", metarJobNotificationTo);
		} catch (MessagingException | IOException | TemplateException e) {
			var message = "Exception while sending failure email."; 
			LOGGER.error(message, e);
			LOGGER.info("Failed to send reomte api success after retry email to: {}", metarJobNotificationTo);
		}
	}

	private String processRefreshIdentifiersFailureTemplate(ApplicationException loadingFromExternalApiException) throws IOException, TemplateException {
		Map<String, Object> templateModelMap = new HashMap<>();
		templateModelMap.put("loadingFromExternalApiException", loadingFromExternalApiException);
		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(REFRESH_AIRPORT_IDENTIFIERS_FAILURE_TEMPLATE), templateModelMap);
	}
	private String processRemoteApiFailureTemplate(HttpRequestTypeEnum httpRequestTypeEnum, ApplicationException loadingFromExternalApiException) throws IOException, TemplateException {
		Map<String, Object> templateModelMap = new HashMap<>();
		templateModelMap.put("httpRequestTypeEnum", httpRequestTypeEnum);
		templateModelMap.put("loadingFromExternalApiException", loadingFromExternalApiException);
		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(REMOTE_API_FAILURE_TEMPLATE), templateModelMap);
	}
	private String processRemoteApiSuccessAfterRetryTemplate(HttpRequestTypeEnum httpRequestTypeEnum, int retryCount) throws IOException, TemplateException {
		Map<String, Object> templateModelMap = new HashMap<>();
		templateModelMap.put("httpRequestTypeEnum", httpRequestTypeEnum);
		templateModelMap.put("retryCount", retryCount);
		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(REMOTE_API_SUCCESS_AFTER_FAILURE_TEMPLATE), templateModelMap);
	}
}
