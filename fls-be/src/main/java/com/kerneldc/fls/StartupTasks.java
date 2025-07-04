package com.kerneldc.fls;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kerneldc.fls.service.airport.AirportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupTasks {

	private final AirportService airportService;

	@EventListener(ApplicationReadyEvent.class)
    public void loadIdentifiers() {
		
		LOGGER.info("Running startup tasks ...");
		
		airportService.loadIdentifiersFromExternalApi();

		LOGGER.info("Startup tasks completed");
    }
}
