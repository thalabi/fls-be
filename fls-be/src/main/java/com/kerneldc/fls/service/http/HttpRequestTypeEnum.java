package com.kerneldc.fls.service.http;

import lombok.Getter;

public enum HttpRequestTypeEnum {
	FLIGHT_LOG_JWT_TOKEN("Flight Log OAuth2 JWT Access Token Service"),
	AIRPORT_IDENTIFIERS("Airport Identifiers Service"),
	FLIGHT_LOG_PENDING_ADD("FlightLogPending Add Service");
	
	@Getter
	String serviceDescription;
	
	HttpRequestTypeEnum(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

}