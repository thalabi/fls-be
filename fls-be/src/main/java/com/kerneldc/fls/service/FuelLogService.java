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
import com.kerneldc.fls.repository.AcParametersRepository;
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
	private final AcParametersRepository acParametersRepository;
	
	@Transactional
	public void addFuelLog(@Valid FuelLogRequest fuelLogRequest) {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info(FUEL_LOG_REQUEST_FORMAT, fuelLogRequest);
		
		if (fuelLogRequest.transactionType() == FuelTransactionTypeEnum.REFUEL && topUp(fuelLogRequest)) {
			LOGGER.info("Topped up");
			fuelLogRepository.deleteInBulkByRegistration(fuelLogRequest.registration());
		}
		
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

	private boolean topUp(@Valid FuelLogRequest fuelLogRequest) {
		var acParameters = acParametersRepository.findByRegistration(fuelLogRequest.registration());
		
		var leftToppedUp = fuelLogRequest.left() + fuelLogRequest.changeInLeft() == acParameters.getEachTankCapacity(); 
		var rightToppedUp = fuelLogRequest.right() + fuelLogRequest.changeInRight() == acParameters.getEachTankCapacity();
		return leftToppedUp && rightToppedUp;
	}

	@Transactional
	public void updateFuelLog(@Valid FuelLogRequest fuelLogRequest) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);

		LOGGER.info(FUEL_LOG_REQUEST_FORMAT, fuelLogRequest);
		var fuelLog = fuelLogRepository.findById(fuelLogRequest.id()).orElseGet(null);
		if (fuelLog == null) {
			throw new ApplicationException(String.format("Log sheet with id [%d] was not found", fuelLogRequest.id()));
		}

		fuelLog.setDate(fuelLogRequest.date());
		fuelLog.setTransactionType(fuelLogRequest.transactionType());
		fuelLog.setLeft(fuelLogRequest.left());
		fuelLog.setRight(fuelLogRequest.right());
		fuelLog.setChangeInLeft(fuelLogRequest.changeInLeft());
		fuelLog.setChangeInRight(fuelLogRequest.changeInRight());

		var fuelPrice = fuelLog.getFuelPrice();
		if (fuelLogRequest.transactionType() == FuelTransactionTypeEnum.REFUEL) {
			if (fuelPrice == null) {
				fuelPrice = new FuelPrice();
				fuelLog.setFuelPrice(fuelPrice);
			}
			fuelPrice.setAirport(fuelLogRequest.airport());
			fuelPrice.setFbo(fuelLogRequest.fbo());
			fuelPrice.setDate(fuelLogRequest.date());
			fuelPrice.setPricePerLitre(fuelLogRequest.pricePerLitre());
			fuelPrice.setComment(fuelLogRequest.comment());
	
		} else {
			if (fuelPrice != null) {
				fuelPriceRepository.delete(fuelPrice);
			}
			fuelLog.setFuelPrice(null);
		}

		fuelLogRepository.save(fuelLog);
		
		LOGGER.info(LOG_END);
	}

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
