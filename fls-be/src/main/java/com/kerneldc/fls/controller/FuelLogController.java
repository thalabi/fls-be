package com.kerneldc.fls.controller;
import static com.kerneldc.fls.AppConstants.LOG_BEGIN;
import static com.kerneldc.fls.AppConstants.LOG_END;

import java.time.OffsetDateTime;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.kerneldc.fls.domain.FuelTransactionTypeEnum;
import com.kerneldc.fls.domain.fuellog.FuelLog;
import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.repository.FuelLogRepository;
import com.kerneldc.fls.service.FuelLogService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController()
@RequestMapping("/protected/fuelLogController")
@RequiredArgsConstructor
@Slf4j
public class FuelLogController {

	public static final String FUEL_LOG_REQUEST_FORMAT = "fuelLogRequest: {}";
	private final FuelLogRepository fuelLogRepository;
	private final FuelLogService fuelLogService;
	
//	public record LogSheetAndFuelLogRequest(Long id, String registration, OffsetDateTime date, String from, String to,
//			Float airtime, Float flightTime, Float leftTankUsed, Float rightTankUsed) {
//	}
	public record FuelLogRequest(Long id, String registration, OffsetDateTime date,
			FuelTransactionTypeEnum transactionType, Float left, Float right, Float changeInLeft, Float changeInRight,
			Float pricePerLitre, String airport, String fbo, String comment, Boolean deleteFuelPrice) {
	}
	
	@GetMapping("/getLastFuelLog")
	public ResponseEntity<FuelLog> getLastFuelLog(@RequestParam @NotBlank String registration) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info("registration: [{}]", registration);
		var fuelLogList = fuelLogRepository.findTopByRegistrationOrderByDateDesc(registration);
		if (CollectionUtils.size(fuelLogList) != 1) {
			throw new ApplicationException(String.format("No fuel_log rows for registration [%s]", registration));
		}
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(fuelLogList.get(0));
	}
	
	@PostMapping("/addFuelLog")
	public ResponseEntity<String> addFuelLog(@Valid @RequestBody FuelLogRequest fuelLogRequest) {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info(FUEL_LOG_REQUEST_FORMAT, fuelLogRequest);
		fuelLogService.addFuelLog(fuelLogRequest);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(StringUtils.EMPTY);
	}

	@PostMapping("/updateFuelLog")
	public ResponseEntity<String> updateFuelLog(@Valid @RequestBody FuelLogRequest fuelLogRequest) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info(FUEL_LOG_REQUEST_FORMAT, fuelLogRequest);
		fuelLogService.updateFuelLog(fuelLogRequest);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(StringUtils.EMPTY);
	}
	
	@PostMapping("/deleteFuelLog")
	public ResponseEntity<String> deleteFuelLog(@Valid @RequestBody FuelLogRequest fuelLogRequest) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);
		LOGGER.info(FUEL_LOG_REQUEST_FORMAT, fuelLogRequest);
		Preconditions.checkArgument(fuelLogRequest.id() != null, "id cannot be null");
		Preconditions.checkArgument(fuelLogRequest.deleteFuelPrice() != null, "deleteFuelPrice cannot be null");
		fuelLogService.deleteFuelLog(fuelLogRequest);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(StringUtils.EMPTY);
	}
}
