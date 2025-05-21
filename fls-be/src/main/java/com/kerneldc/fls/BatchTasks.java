package com.kerneldc.fls;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.service.AirportService;
import com.kerneldc.fls.util.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchTasks {
	
	private final AirportService airportService;
	private final EmailService emailService;

	@EventListener(ApplicationReadyEvent.class)
    public void loadIdentifiers() {
		// do not run if in test mode
		if (testMode()) {
			return;
		}
		try {
			airportService.loadIdentifiersFromExternalApi();
		} catch (ApplicationException e) {
			var message = "Failed to load airport identifiers from external api";
			e.addMessage(message);
			e.printStackTrace();
			LOGGER.warn(message);
			LOGGER.info("Sending failure email");
			emailService.sendLoadIdentifiersFailureEmail(e);
		}
    }
	
	@Scheduled(cron = "0 30 7 * * SUN") // Every Sun at 7:30 AM (after Jenkins job that enriches airport table)
//	@Scheduled(cron = "0 57 18 * * *") // Run at 11:01 AM
	public void refreshIdentifiers() {
		try {
			airportService.refreshIdentifiersFromExternalApi();
		} catch (ApplicationException e) {
			var message = "Failed to refresh airport identifiers from external api";
			e.addMessage(message);
			e.printStackTrace();
			LOGGER.warn(message);
			LOGGER.info("Sending failure email");
			emailService.sendRefreshIdentifiersFailureEmail(e);
		}
	}
	
	private boolean testMode() {     
	    for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
	        if (ste.getClassName().contains("org.springframework.test.context")) {
	            return true;
	        }
	    }       
	    return false;
	}
}
