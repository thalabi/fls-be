package com.kerneldc.fls;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.kerneldc.fls.exeption.LoadingFromExternalApiException;
import com.kerneldc.fls.service.AirportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupTasks {
	
	private final AirportService airportService; 
	private final Environment environment;

	@EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
		// do not run if in test mode otherwise
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
		
	private boolean testMode() {     
	    for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
	        if (ste.getClassName().contains("org.springframework.test.context")) {
	            return true;
	        }
	    }       
	    return false;
	}
}
