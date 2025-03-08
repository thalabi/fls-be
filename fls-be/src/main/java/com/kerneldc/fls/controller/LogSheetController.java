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

import com.kerneldc.fls.service.LogSheetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController()
@RequestMapping("/protected/logSheetController")
@RequiredArgsConstructor
@Slf4j
public class LogSheetController {

	private final LogSheetService logSheetService;
	
	public record LogSheetRequest(String registration, OffsetDateTime date, Float airtime, Float flightTime, Float leftTankUsed, Float rightTankUsed) {}

	@PostMapping("/addLogSheet")
	public ResponseEntity<String> addLogSheet(@Valid @RequestBody LogSheetRequest logSheetRequest) {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info("logSheetRequest: {}", logSheetRequest);
		logSheetService.addLogSheet(logSheetRequest);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(StringUtils.EMPTY);
	}
}
