package com.kerneldc.fls.util;

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

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

	@Value("${application.email.airportIdentifierTaskNotificationFrom}")
	private String airportIdentifierTaskNotificationFrom;
	@Value("${application.email.airportIdentifierTaskNotificationTo}")
	private String airportIdentifierTaskNotificationTo;
	
//	private static final String LOAD_AIRPORT_IDENTIFIERS_FAILURE_SUBJECT = "Load Airport Identifiers Failure";
//	private static final String LOAD_AIRPORT_IDENTIFIERS_FAILURE_TEMPLATE = "loadAirportIdentifiersFailure.ftlh";
	private static final String REFRESH_AIRPORT_IDENTIFIERS_FAILURE_SUBJECT = "Refresh Airport Identifiers Failure";
	private static final String REFRESH_AIRPORT_IDENTIFIERS_FAILURE_TEMPLATE = "refreshAirportIdentifiersFailure.ftlh";
	private JavaMailSender javaMailSender;
	private Configuration freeMarkerConfiguration;
	
	public EmailService(JavaMailSender emailSender, Configuration freeMarkerConfiguration) {
		this.javaMailSender = emailSender;
		this.freeMarkerConfiguration = freeMarkerConfiguration;
	}

//	public void sendLoadIdentifiersFailureEmail(ApplicationException loadingFromExternalApiException) {
//		var mimeMessage = javaMailSender.createMimeMessage();
//		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
//		try {
//			mimeMessageHelper.setFrom(airportIdentifierTaskNotificationFrom);
//			mimeMessageHelper.setTo(InternetAddress.parse(airportIdentifierTaskNotificationTo));
//			mimeMessageHelper.setSubject(LOAD_AIRPORT_IDENTIFIERS_FAILURE_SUBJECT);
//			mimeMessageHelper.setText(processLoadIdentifiersFailureTemplate(loadingFromExternalApiException), true);
//			javaMailSender.send(mimeMessage);
//			LOGGER.info("Sent load identifiers failure email to: {}", airportIdentifierTaskNotificationTo);
//		} catch (MessagingException | IOException | TemplateException e) {
//			var message = "Exception while sending failure email."; 
//			LOGGER.error(message, e);
//			LOGGER.info("Failed to send load identifiers failure email to: {}", airportIdentifierTaskNotificationTo);
//		}
//	}
//	private String processLoadIdentifiersFailureTemplate(ApplicationException loadingFromExternalApiException) throws IOException, TemplateException {
//		Map<String, Object> templateModelMap = new HashMap<>();
//		templateModelMap.put("loadingFromExternalApiException", loadingFromExternalApiException);
//		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(LOAD_AIRPORT_IDENTIFIERS_FAILURE_TEMPLATE), templateModelMap);
//	}
	
	public void sendRefreshIdentifiersFailureEmail(ApplicationException loadingFromExternalApiException) {
		var mimeMessage = javaMailSender.createMimeMessage();
		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
		try {
			mimeMessageHelper.setFrom(airportIdentifierTaskNotificationFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(airportIdentifierTaskNotificationTo));
			mimeMessageHelper.setSubject(REFRESH_AIRPORT_IDENTIFIERS_FAILURE_SUBJECT);
			mimeMessageHelper.setText(processRefreshIdentifiersFailureTemplate(loadingFromExternalApiException), true);
			javaMailSender.send(mimeMessage);
			LOGGER.info("Sent refresh identifiers failure email to: {}", airportIdentifierTaskNotificationTo);
		} catch (MessagingException | IOException | TemplateException e) {
			var message = "Exception while sending failure email."; 
			LOGGER.error(message, e);
			LOGGER.info("Failed to send refresh identifiers failure email to: {}", airportIdentifierTaskNotificationTo);
		}
	}
	private String processRefreshIdentifiersFailureTemplate(ApplicationException loadingFromExternalApiException) throws IOException, TemplateException {
		Map<String, Object> templateModelMap = new HashMap<>();
		templateModelMap.put("loadingFromExternalApiException", loadingFromExternalApiException);
		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(REFRESH_AIRPORT_IDENTIFIERS_FAILURE_TEMPLATE), templateModelMap);
	}
}
