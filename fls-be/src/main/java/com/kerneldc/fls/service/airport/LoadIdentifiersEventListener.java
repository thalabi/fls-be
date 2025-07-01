package com.kerneldc.fls.service.airport;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kerneldc.fls.exeption.ApplicationException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoadIdentifiersEventListener {

	private final AirportIdentifierLoader airportIdentifierLoader;

	@Async
	@EventListener
	public void loadEventListener(LoadIdentifiersEvent loadIdentifiersEvent) throws ApplicationException {
		airportIdentifierLoader.loadRemotely(loadIdentifiersEvent);
	}
}
