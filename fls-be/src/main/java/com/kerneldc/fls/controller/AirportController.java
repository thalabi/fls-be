package com.kerneldc.fls.controller;

import static com.kerneldc.fls.AppConstants.LOG_BEGIN;
import static com.kerneldc.fls.AppConstants.LOG_END;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.service.AirportService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController()
@RequestMapping("/protected/airportController")
@RequiredArgsConstructor
@Slf4j
public class AirportController {

	private final AirportService airportService; 

	@GetMapping("/isIdentifierValid")
	public ResponseEntity<Map<String, Boolean>> isIdentifierValid(@RequestParam @NotBlank String identifier) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info("identifier: [{}]", identifier);
		var valid = airportService.isIdentifierValid(identifier.toUpperCase());
		LOGGER.info("valid: [{}]", valid);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(Map.of("valid", valid));
	}

}
