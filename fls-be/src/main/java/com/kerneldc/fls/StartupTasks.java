package com.kerneldc.fls;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kerneldc.fls.service.airport.AirportService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StartupTasks {

	private final AirportService airportService;

	@EventListener(ApplicationReadyEvent.class)
    public void loadIdentifiers() {
		// do not run if in test mode
		if (testMode()) {
			return;
		}
			airportService.loadIdentifiersFromExternalApi();
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
