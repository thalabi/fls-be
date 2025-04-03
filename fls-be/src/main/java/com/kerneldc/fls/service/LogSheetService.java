package com.kerneldc.fls.service;
import static com.kerneldc.fls.AppConstants.LOG_BEGIN;
import static com.kerneldc.fls.AppConstants.LOG_END;
import static com.kerneldc.fls.controller.LogSheetController.LOG_SHEET_REQUEST_FORMAT;

import org.springframework.stereotype.Service;

import com.kerneldc.fls.controller.LogSheetController.LogSheetAndFuelLogRequest;
import com.kerneldc.fls.controller.LogSheetController.LogSheetRequest;
import com.kerneldc.fls.domain.EnginePositionEnum;
import com.kerneldc.fls.domain.FuelTransactionTypeEnum;
import com.kerneldc.fls.domain.enginelog.EngineLog;
import com.kerneldc.fls.domain.fuellog.FuelLog;
import com.kerneldc.fls.domain.journeylog.JourneyLog;
import com.kerneldc.fls.domain.logsheet.LogSheet;
import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.repository.FuelLogRepository;
import com.kerneldc.fls.repository.LogSheetRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogSheetService {

	private final LogSheetRepository logSheetRepository;
	private final FuelLogRepository fuelLogRepository;
	
	@Transactional
	public void addLogSheetAndFuelLog(@Valid LogSheetAndFuelLogRequest logSheetAndFuelLogRequest) {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info("logSheetAndFuelLogRequest: [{}]", logSheetAndFuelLogRequest);
		
		var registration = logSheetAndFuelLogRequest.registration();
		var date = logSheetAndFuelLogRequest.date();
		var from = logSheetAndFuelLogRequest.from();
		var to = logSheetAndFuelLogRequest.to();
		var takeoffTime = logSheetAndFuelLogRequest.takeoffTime();
		var landingTime = logSheetAndFuelLogRequest.landingTime();
		var airtime = logSheetAndFuelLogRequest.airtime();
		
		// log_sheet row
		var logSheet = new LogSheet();
		logSheet.setRegistration(registration);
		logSheet.setDate(date);
		logSheet.setFrom(from);
		logSheet.setTo(to);
		logSheet.setTakeoffTime(takeoffTime);
		logSheet.setLandingTime(landingTime);
		logSheet.setAirtime(logSheetAndFuelLogRequest.airtime());
		logSheet.setFlightTime(logSheetAndFuelLogRequest.flightTime());
		
		// get last fuel_log row
		var lastFuelLogList = fuelLogRepository.findTopByRegistrationOrderByDateDesc(registration);
		if (lastFuelLogList.isEmpty()) {
			throw new IllegalStateException(String.format("There should be at least on one row for registartion [%s] in fuel_log table", registration));
		}
		var lastFuelLog = lastFuelLogList.get(0);
		LOGGER.info("lastFuelLog: {}", lastFuelLog);
		
		// journey_log row
		var journeyLog = new JourneyLog();
		journeyLog.setRegistration(registration);
		journeyLog.setDate(date);
		journeyLog.setFrom(from);
		journeyLog.setTo(to);
		journeyLog.setTakeoffTime(takeoffTime);
		journeyLog.setLandingTime(landingTime);
		journeyLog.setAirtime(airtime);
		logSheet.setJourneyLog(journeyLog);

		// engine_log row
		var engineLog  = new EngineLog();
		engineLog.setRegistration(registration);
		engineLog.setPosition(EnginePositionEnum.CENTER);
		engineLog.setDate(date);
		engineLog.setAirtime(airtime);
		logSheet.setEngineLog(engineLog);

		logSheetRepository.save(logSheet);

		// insert fuelLog row
		var fuelLog = new FuelLog();
		fuelLog.setRegistration(registration);
		fuelLog.setDate(date);
		fuelLog.setTransactionType(FuelTransactionTypeEnum.FLIGHT);
		fuelLog.setLeft(lastFuelLog.getLeft() + lastFuelLog.getChangeInLeft());
		fuelLog.setRight(lastFuelLog.getRight() + lastFuelLog.getChangeInRight());
		fuelLog.setChangeInLeft(-1 * logSheetAndFuelLogRequest.leftTankUsed());
		fuelLog.setChangeInRight(-1 * logSheetAndFuelLogRequest.rightTankUsed());
		fuelLogRepository.save(fuelLog);
		
    	LOGGER.info(LOG_END);
	}

	@Transactional
	public void addLogSheet(LogSheetRequest logSheetRequest) {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info(LOG_SHEET_REQUEST_FORMAT, logSheetRequest);
		
		var registration = logSheetRequest.registration();
		var date = logSheetRequest.date();
		var from = logSheetRequest.from();
		var to = logSheetRequest.to();
		var takeoffTime = logSheetRequest.takeoffTime();
		var landingTime = logSheetRequest.landingTime();
		var airtime = logSheetRequest.airtime();
		
		// log_sheet row
		var logSheet = new LogSheet();
		logSheet.setRegistration(registration);
		logSheet.setDate(date);
		logSheet.setFrom(from);
		logSheet.setTo(to);
		logSheet.setTakeoffTime(takeoffTime);
		logSheet.setLandingTime(landingTime);
		logSheet.setAirtime(airtime);
		logSheet.setFlightTime(logSheetRequest.flightTime());
		
		// journey_log row
		var journeyLog = new JourneyLog();
		journeyLog.setRegistration(registration);
		journeyLog.setDate(date);
		journeyLog.setFrom(from);
		journeyLog.setTo(to);
		journeyLog.setTakeoffTime(takeoffTime);
		journeyLog.setLandingTime(landingTime);
		journeyLog.setAirtime(airtime);
		logSheet.setJourneyLog(journeyLog);

		// engine_log row
		var engineLog = new EngineLog();
		engineLog.setRegistration(registration);
		engineLog.setPosition(EnginePositionEnum.CENTER);
		engineLog.setDate(date);
		engineLog.setAirtime(airtime);
		logSheet.setEngineLog(engineLog);
		
		logSheetRepository.save(logSheet);
		
    	LOGGER.info(LOG_END);
	}

	@Transactional
	public void updateLogSheet(@Valid LogSheetRequest logSheetRequest) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info(LOG_SHEET_REQUEST_FORMAT, logSheetRequest);
		var logSheetOptional = logSheetRepository.findById(logSheetRequest.id());
		if (logSheetOptional.isEmpty()) {
			throw new ApplicationException(String.format("Log sheet with id [%d] was not found", logSheetRequest.id()));
		}
		var logSheet = logSheetOptional.get();

		var registration = logSheetRequest.registration();
		var date = logSheetRequest.date();
		var from = logSheetRequest.from();
		var to = logSheetRequest.to();
		var takeoffTime = logSheetRequest.takeoffTime();
		var landingTime = logSheetRequest.landingTime();
		var airtime = logSheetRequest.airtime();

		logSheet.setRegistration(registration);
		logSheet.setDate(date);
		logSheet.setFrom(from);
		logSheet.setTo(to);
		logSheet.setTakeoffTime(takeoffTime);
		logSheet.setLandingTime(landingTime);
		logSheet.setAirtime(airtime);
		logSheet.setFlightTime(logSheetRequest.flightTime());
		
		logSheet.getJourneyLog().setRegistration(registration);
		logSheet.getJourneyLog().setDate(date);
		logSheet.getJourneyLog().setFrom(from);
		logSheet.getJourneyLog().setTo(to);
		logSheet.getJourneyLog().setTakeoffTime(takeoffTime);
		logSheet.getJourneyLog().setLandingTime(landingTime);
		logSheet.getJourneyLog().setAirtime(airtime);

		logSheet.getEngineLog().setRegistration(registration);
		logSheet.getEngineLog().setDate(date);
		logSheet.getEngineLog().setAirtime(airtime);

		
		LOGGER.info(LOG_END);
	}

	@Transactional
	public void deleteLogSheet(LogSheetRequest logSheetRequest) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info(LOG_SHEET_REQUEST_FORMAT, logSheetRequest);
		var logSheetOptional = logSheetRepository.findById(logSheetRequest.id());
		if (logSheetOptional.isEmpty()) {
			throw new ApplicationException(String.format("Log sheet with id [%d] was not found", logSheetRequest.id()));
		}
		logSheetRepository.deleteById(logSheetRequest.id());
		
		
    	LOGGER.info(LOG_END);
	}

}
