package com.kerneldc.fls.service.logsheet;
import static com.kerneldc.fls.AppConstants.LOG_BEGIN;
import static com.kerneldc.fls.AppConstants.LOG_END;
import static com.kerneldc.fls.controller.LogSheetController.LOG_SHEET_REQUEST_FORMAT;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.fls.controller.LogSheetController.LogSheetAndFuelLogRequest;
import com.kerneldc.fls.controller.LogSheetController.LogSheetRequest;
import com.kerneldc.fls.domain.EnginePositionEnum;
import com.kerneldc.fls.domain.FuelTransactionTypeEnum;
import com.kerneldc.fls.domain.enginelog.EngineLog;
import com.kerneldc.fls.domain.fuellog.FuelLog;
import com.kerneldc.fls.domain.journeylog.JourneyLog;
import com.kerneldc.fls.domain.logsheet.LogSheet;
import com.kerneldc.fls.domain.remoteapicall.RemoteApiCall;
import com.kerneldc.fls.domain.remoteapicalllog.RemoteApiCallLog;
import com.kerneldc.fls.domain.remoteapicalllog.RemoteApiCallLog.RetryStatusEnum;
import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.exeption.ApplicationRuntimeException;
import com.kerneldc.fls.repository.AcParametersRepository;
import com.kerneldc.fls.repository.FuelLogRepository;
import com.kerneldc.fls.repository.LogSheetRepository;
import com.kerneldc.fls.repository.RemoteApiCallLogRepository;
import com.kerneldc.fls.repository.RemoteApiCallRepository;
import com.kerneldc.fls.service.http.HttpRequestTypeEnum;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogSheetService {

	private final AcParametersRepository acParametersRepository;
	private final LogSheetRepository logSheetRepository;
	private final FuelLogRepository fuelLogRepository;
	private final RemoteApiCallLogRepository remoteApiCallLogRepository;
	private final RemoteApiCallRepository remoteApiCallRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final ObjectMapper objectMapper;
	
	
	public record FlightLogPendingVo (String flightDate, String routeFrom, String routeTo, Float flightTime, String registration, String makeModel) implements Serializable {}

	@Transactional
	public void addLogSheetAndFuelLog(@Valid LogSheetAndFuelLogRequest logSheetAndFuelLogRequest) throws ApplicationRuntimeException {
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
		
		// publish event to trigger remote api call
		var now = OffsetDateTime.now();
		var flightLogPendingVo = toFlightLogPendingVo(logSheetAndFuelLogRequest);
		
		var remoteApiCall = new RemoteApiCall();
		remoteApiCall.setRequest(HttpRequestTypeEnum.FLIGHT_LOG_PENDING_ADD);
		remoteApiCall.setParameters(toJson(logSheetAndFuelLogRequest));
		remoteApiCall.setTimestamp(now);
		remoteApiCallRepository.save(remoteApiCall);
		
		var remoteApiCallLog = new RemoteApiCallLog();
		remoteApiCallLog.setStatus(RetryStatusEnum.NEVER_ATTEMPTED);
		remoteApiCallLog.setRemoteApiCall(remoteApiCall);
		remoteApiCallLog.setTimestamp(now);
		remoteApiCallLogRepository.save(remoteApiCallLog);
		
		var logSheetAddedEvent = new LogSheetAddedEvent(this, remoteApiCall, flightLogPendingVo);
		eventPublisher.publishEvent(logSheetAddedEvent);
		
    	LOGGER.info(LOG_END);
	}
	
	private FlightLogPendingVo toFlightLogPendingVo(LogSheetAndFuelLogRequest logSheetAndFuelLogRequest) {
		var flightDate = logSheetAndFuelLogRequest.date().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
		var acParameters = acParametersRepository.findByRegistration(logSheetAndFuelLogRequest.registration());
		String makeModel;
		if (acParameters != null) {
			makeModel = acParameters.getMakeModel();
		} else {
			LOGGER.warn("Could not find ac_parameters row with registration [{}]", logSheetAndFuelLogRequest.registration());
			makeModel = StringUtils.EMPTY;
		}
		return new FlightLogPendingVo(flightDate, logSheetAndFuelLogRequest.from(), logSheetAndFuelLogRequest.to(),
				logSheetAndFuelLogRequest.flightTime(), logSheetAndFuelLogRequest.registration(), makeModel);
	}
	private String toJson(LogSheetAndFuelLogRequest logSheetAndFuelLogRequest) throws ApplicationRuntimeException {
		try {
			return objectMapper.writeValueAsString(logSheetAndFuelLogRequest);
		} catch (JsonProcessingException e) {
			throw new ApplicationRuntimeException(e);
		}
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
	public void updateLogSheet(@Valid LogSheetRequest logSheetRequest) throws ApplicationRuntimeException {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info(LOG_SHEET_REQUEST_FORMAT, logSheetRequest);
		var logSheetOptional = logSheetRepository.findById(logSheetRequest.id());
		if (logSheetOptional.isEmpty()) {
			throw new ApplicationRuntimeException(String.format("Log sheet with id [%d] was not found", logSheetRequest.id()));
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
