package com.kerneldc.fls.service.logsheet;

import java.time.Clock;

import org.springframework.context.ApplicationEvent;

import com.kerneldc.fls.domain.remoteapicall.RemoteApiCall;
import com.kerneldc.fls.service.logsheet.LogSheetService.FlightLogPendingVo;

import lombok.Getter;

public class LogSheetAddedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	@Getter
	private final RemoteApiCall remoteApiCall;
	@Getter
	private final FlightLogPendingVo flightLogPendingVo;
	
	public LogSheetAddedEvent(Object source, RemoteApiCall remoteApiCall, FlightLogPendingVo flightLogPendingVo) {
		super(source);
		this.remoteApiCall = remoteApiCall;
		this.flightLogPendingVo = flightLogPendingVo;
	}

	public LogSheetAddedEvent(Object source, Clock clock, RemoteApiCall remoteApiCall, FlightLogPendingVo flightLogPendingVo) {
		super(source, clock);
		this.remoteApiCall = remoteApiCall;
		this.flightLogPendingVo = flightLogPendingVo;
	}

}
