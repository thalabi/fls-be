package com.kerneldc.fls;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kerneldc.fls.exeption.LoadingFromExternalApiException;
import com.kerneldc.fls.service.AirportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchTasks {
	
	private final AirportService airportService; 

	@EventListener(ApplicationReadyEvent.class)
    public void loadIdentifiers() {
		// do not run if in test mode
		if (testMode()) {
			return;
		}
		try {
			airportService.loadIdentifiersFromExternalApi();
		} catch (LoadingFromExternalApiException e) {
			e.addMessage("Failed to load airport identifiers from external api");
			e.printStackTrace();
		}
    }
	
	@Scheduled(cron = "0 30 7 * * SUN") // Every Sun at 7:30 AM (after Jenkins job that enriches airport table)
//	@Scheduled(cron = "0 1 11 * * *") // Run at 11:01 AM
	public void refreshIdentifiers() {
		try {
			airportService.refreshIdentifiersFromExternalApi();
		} catch (LoadingFromExternalApiException e) {
			e.addMessage("Failed to refresh airport identifiers from external api");
			e.printStackTrace();
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
