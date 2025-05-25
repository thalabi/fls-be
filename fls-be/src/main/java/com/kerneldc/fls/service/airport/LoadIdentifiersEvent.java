package com.kerneldc.fls.service.airport;

import java.time.Clock;

import org.springframework.context.ApplicationEvent;

import com.kerneldc.fls.domain.remoteapicall.RemoteApiCall;

import lombok.Getter;

public class LoadIdentifiersEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	@Getter
	private final RemoteApiCall remoteApiCall;
	
	public LoadIdentifiersEvent(Object source, RemoteApiCall remoteApiCall) {
		super(source);
		this.remoteApiCall = remoteApiCall;
	}

	public LoadIdentifiersEvent(Object source, Clock clock, RemoteApiCall remoteApiCall) {
		super(source, clock);
		this.remoteApiCall = remoteApiCall;
	}

}
