package com.kerneldc.fls.service;
import static com.kerneldc.fls.AppConstants.LOG_BEGIN;
import static com.kerneldc.fls.AppConstants.LOG_END;

import org.springframework.stereotype.Service;

import com.kerneldc.fls.controller.LogSheetController.LogSheetRequest;
import com.kerneldc.fls.domain.fuellog.FuelLog;
import com.kerneldc.fls.domain.logsheet.LogSheet;
import com.kerneldc.fls.repository.AcParametersRepository;
import com.kerneldc.fls.repository.FuelLogRepository;
import com.kerneldc.fls.repository.LogSheetRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogSheetService {

	private final LogSheetRepository logSheetRepository;
	private final FuelLogRepository fuelLogRepository;
	private final AcParametersRepository acParametersRepository;
	
	@Transactional
	public void addLogSheet(LogSheetRequest logSheetRequest) {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info("logSheetRequest: {}", logSheetRequest);
		
		var registration = logSheetRequest.registration();
		var date = logSheetRequest.date();
		
		// insert in log_sheet table
		var logSheet = new LogSheet();
		logSheet.setRegistration(registration);
		logSheet.setDate(date);
		logSheet.setAirtime(logSheetRequest.airtime());
		logSheet.setFlightTime(logSheetRequest.flightTime());
		logSheetRepository.save(logSheet);
		
		// get last fuel_log row
		var lastFuelLogList = fuelLogRepository.findTopByRegistrationOrderByDateDesc(registration);
		if (lastFuelLogList.isEmpty()) {
			throw new IllegalStateException(String.format("There should be at least on one row for registartion [%s] in fuel_log table", registration));
		}
		var lastFuelLog = lastFuelLogList.get(0);
		LOGGER.info("lastFuelLog: {}", lastFuelLog);
		
		// insert a new fuelLog row
		var fuelLog = new FuelLog();
		fuelLog.setRegistration(registration);
		fuelLog.setDate(date);
		fuelLog.setLeft(lastFuelLog.getLeft() + lastFuelLog.getChangeInLeft());
		fuelLog.setRight(lastFuelLog.getRight() + lastFuelLog.getChangeInRight());
		fuelLog.setChangeInLeft(-1 * logSheetRequest.leftTankUsed());
		fuelLog.setChangeInRight(-1 * logSheetRequest.rightTankUsed());
		fuelLogRepository.save(fuelLog);
		
		// update ac_parameters row
		var acParameters = acParametersRepository.findByRegistration(registration);
		if (acParameters == null) {
			throw new IllegalStateException(String.format("There should be at least on one row for registartion [%s] in ac_parameters table", registration));
		}
		acParameters.setInitialTsn(acParameters.getInitialTsn() + logSheetRequest.airtime());
		acParameters.setInitialTsmoh(acParameters.getInitialTsmoh() + logSheetRequest.airtime());
		acParametersRepository.save(acParameters);
		
    	LOGGER.info(LOG_END);

	}
}
