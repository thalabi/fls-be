package com.kerneldc.fls.controller;
import static com.kerneldc.fls.AppConstants.LOG_BEGIN;
import static com.kerneldc.fls.AppConstants.LOG_END;

import java.time.OffsetDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.service.logsheet.LogSheetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController()
@RequestMapping("/protected/logSheetController")
@RequiredArgsConstructor
@Slf4j
public class LogSheetController {

	public static final String LOG_SHEET_REQUEST_FORMAT = "logSheetRequest: {}";
	private final LogSheetService logSheetService;
	
	public record LogSheetAndFuelLogRequest(Long id, String registration, OffsetDateTime date, String from, String to,
			OffsetDateTime takeoffTime, OffsetDateTime landingTime,
			Float airtime, Float flightTime, Float leftTankUsed, Float rightTankUsed) {
	}
	public record LogSheetRequest(Long id, String registration, OffsetDateTime date, String from, String to,
			OffsetDateTime takeoffTime, OffsetDateTime landingTime,
			Float airtime, Float flightTime) {
	}
	
	@PostMapping("/addLogSheetAndFuelLog")
	public ResponseEntity<String> addLogSheetAndFuelLog(@Valid @RequestBody LogSheetAndFuelLogRequest logSheetAndFuelLogRequest) throws JsonProcessingException {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info("logSheetAndFuelLogRequest: {}", logSheetAndFuelLogRequest);
		logSheetService.addLogSheetAndFuelLog(logSheetAndFuelLogRequest);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(StringUtils.EMPTY);
	}

	@PostMapping("/addLogSheet")
	public ResponseEntity<String> addLogSheet(@Valid @RequestBody LogSheetRequest logSheetRequest) {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info(LOG_SHEET_REQUEST_FORMAT, logSheetRequest);
		logSheetService.addLogSheet(logSheetRequest);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(StringUtils.EMPTY);
	}
	
	@PostMapping("/updateLogSheet")
	public ResponseEntity<String> updateLogSheet(@Valid @RequestBody LogSheetRequest logSheetRequest) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info(LOG_SHEET_REQUEST_FORMAT, logSheetRequest);
		logSheetService.updateLogSheet(logSheetRequest);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(StringUtils.EMPTY);
	}
	
	@PostMapping("/deleteLogSheet")
	public ResponseEntity<String> deleteLogSheet(@Valid @RequestBody LogSheetRequest logSheetRequest) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info(LOG_SHEET_REQUEST_FORMAT, logSheetRequest);
		Preconditions.checkArgument(logSheetRequest.id() != null, "id cannot be null");
		logSheetService.deleteLogSheet(logSheetRequest);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(StringUtils.EMPTY);
	}
}
