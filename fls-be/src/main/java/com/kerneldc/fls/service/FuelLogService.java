package com.kerneldc.fls.service;
import static com.kerneldc.fls.AppConstants.LOG_BEGIN;
import static com.kerneldc.fls.AppConstants.LOG_END;
import static com.kerneldc.fls.controller.FuelLogController.FUEL_LOG_REQUEST_FORMAT;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import com.kerneldc.fls.controller.FuelLogController.FuelLogRequest;
import com.kerneldc.fls.domain.FuelTransactionTypeEnum;
import com.kerneldc.fls.domain.fuellog.FuelLog;
import com.kerneldc.fls.domain.fuelprice.FuelPrice;
import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.repository.FuelLogRepository;
import com.kerneldc.fls.repository.FuelPriceRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelLogService {

	private final FuelLogRepository fuelLogRepository;
	private final FuelPriceRepository fuelPriceRepository;
	
	@Transactional
	public void addFuelLog(@Valid FuelLogRequest fuelLogRequest) {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info(FUEL_LOG_REQUEST_FORMAT, fuelLogRequest);
		
		var date = fuelLogRequest.date();
		
		// fuel_log row
		var fuelLog = new FuelLog();
		fuelLog.setRegistration(fuelLogRequest.registration());
		fuelLog.setDate(date);
		fuelLog.setTransactionType(fuelLogRequest.transactionType());
		fuelLog.setLeft(fuelLogRequest.left());
		fuelLog.setRight(fuelLogRequest.right());
		fuelLog.setChangeInLeft(fuelLogRequest.changeInLeft());
		fuelLog.setChangeInRight(fuelLogRequest.changeInRight());
		
		if (fuelLogRequest.transactionType() == FuelTransactionTypeEnum.REFUEL) {
			// fuel_price row
			var fuelPrice = new FuelPrice();
			fuelPrice.setAirport(fuelLogRequest.airport());
			fuelPrice.setFbo(fuelLogRequest.fbo());
			fuelPrice.setDate(date);
			fuelPrice.setPricePerLitre(fuelLogRequest.pricePerLitre());
			fuelPrice.setComment(fuelLogRequest.comment());
	
			fuelLog.setFuelPrice(fuelPrice);
		}

		fuelLogRepository.save(fuelLog);

    	LOGGER.info(LOG_END);
		
	}
/*
	@Transactional
	public void addLogSheet(LogSheetRequest logSheetRequest) {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info(LOG_SHEET_REQUEST_FORMAT, logSheetRequest);
		
		var registration = logSheetRequest.registration();
		var date = logSheetRequest.date();
		var from = logSheetRequest.from();
		var to = logSheetRequest.to();
		var airtime = logSheetRequest.airtime();
		
		// log_sheet row
		var logSheet = new LogSheet();
		logSheet.setRegistration(registration);
		logSheet.setDate(date);
		logSheet.setFrom(from);
		logSheet.setTo(to);
		logSheet.setAirtime(airtime);
		logSheet.setFlightTime(logSheetRequest.flightTime());
		
		// journey_log row
//		if (BooleanUtils.isTrue(logSheetRequest.updateJourneyLog())) {
			var journeyLog = new JourneyLog();
			journeyLog.setRegistration(registration);
			journeyLog.setDate(date);
			journeyLog.setFrom(from);
			journeyLog.setTo(to);
			journeyLog.setAirtime(airtime);
			logSheet.setJourneyLog(journeyLog);
//		}

		// engine_log row
//		if (BooleanUtils.isTrue(logSheetRequest.updateEngineLog())) {
			var engineLog = new EngineLog();
			engineLog.setRegistration(registration);
			engineLog.setPosition(EnginePositionEnum.CENTER);
			engineLog.setDate(date);
			engineLog.setAirtime(airtime);
			logSheet.setEngineLog(engineLog);
//		}
		
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
		var airtime = logSheetRequest.airtime();

		logSheet.setRegistration(registration);
		logSheet.setDate(date);
		logSheet.setFrom(from);
		logSheet.setTo(to);
		logSheet.setAirtime(airtime);
		logSheet.setFlightTime(logSheetRequest.flightTime());
		
//		if (BooleanUtils.isTrue(logSheetRequest.updateJourneyLog())) {
			logSheet.getJourneyLog().setRegistration(registration);
			logSheet.getJourneyLog().setDate(date);
			logSheet.getJourneyLog().setFrom(from);
			logSheet.getJourneyLog().setTo(to);
			logSheet.getJourneyLog().setAirtime(airtime);
//		}

//		if (BooleanUtils.isTrue(logSheetRequest.updateEngineLog())) {
			logSheet.getEngineLog().setRegistration(registration);
			logSheet.getEngineLog().setDate(date);
			logSheet.getEngineLog().setAirtime(airtime);
//		}

		logSheetRepository.save(logSheet);
		
		LOGGER.info(LOG_END);
	}

 */
	@Transactional
	public void deleteFuelLog(FuelLogRequest fuelLogRequest) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info(FUEL_LOG_REQUEST_FORMAT, fuelLogRequest);
		var fuelLog = fuelLogRepository.findById(fuelLogRequest.id()).orElse(null);
		if (fuelLog == null) {
			throw new ApplicationException(String.format("fuel_log row with id [%d] is not found", fuelLogRequest.id()));
		}
		fuelLogRepository.deleteById(fuelLogRequest.id());
		if (BooleanUtils.isTrue(fuelLogRequest.deleteFuelPrice())) {
			var fuelPriceId = fuelLog.getFuelPrice().getId();
			var fuelPrice = fuelPriceRepository.findById(fuelPriceId).orElse(null);
			if (fuelPrice == null) {
				throw new ApplicationException(String.format("fuel_price row with id [%d] is not found", fuelPriceId));
			}
			fuelPriceRepository.deleteById(fuelPriceId);
		}
		
    	LOGGER.info(LOG_END);
	}
}
