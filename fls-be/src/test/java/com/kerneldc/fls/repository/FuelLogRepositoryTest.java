package com.kerneldc.fls.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.kerneldc.fls.domain.FuelTransactionTypeEnum;
import com.kerneldc.fls.domain.fuellog.FuelLog;
import com.kerneldc.fls.domain.fuelprice.FuelPrice;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.NONE) // use the datasource properties to create in mem h2 database instead of a default embedded database
class FuelLogRepositoryTest {

	private static final String REGISTRATION_1 = "C-GQGD";
	private static final OffsetDateTime DATE_1 = OffsetDateTime.of(2025,  3, 28, 16, 13, 30, 0, ZoneOffset.UTC);
	private static final String AIRPORT_1 = "CNF4";
	private static final String FBO_1 = "Kawartha Lakes Municipal Airport"; 
	private static final Float PRICE_1 = 2.65f;
	private static final Float FULL_TANK = 24f;
	
	@Autowired
	private FuelLogRepository fuelLogRepository;
	@Autowired
	private FuelPriceRepository fuelPriceRepository;

	@BeforeEach
	void init() {
		var fuelLog = new FuelLog();
		fuelLog.setRegistration(REGISTRATION_1);
		fuelLog.setDate(DATE_1);
		fuelLog.setTransactionType(FuelTransactionTypeEnum.REFUEL);
		fuelLog.setLeft(0f);
		fuelLog.setRight(0f);
		fuelLog.setChangeInLeft(FULL_TANK);
		fuelLog.setChangeInRight(FULL_TANK);
		var fuelPrice = new FuelPrice();
		fuelPrice.setAirport(AIRPORT_1);
		fuelPrice.setFbo(FBO_1);
		fuelPrice.setDate(DATE_1);
		fuelPrice.setPricePerLitre(PRICE_1);
		fuelPrice.setAirport(AIRPORT_1);
		fuelPrice.setFbo(FBO_1);
		fuelLog.setFuelPrice(fuelPrice);
		fuelLogRepository.save(fuelLog);
	}

	@Test
	void testFuelLogRepositorAreNotNull() {
		assertThat(fuelLogRepository, notNullValue());
		assertThat(fuelPriceRepository, notNullValue());
	}

	@Test
	void testAddFluelLog_willAddFuelLogAndFuelPrice() {
		var fuelLogList = fuelLogRepository.findByRegistrationAndDate(REGISTRATION_1, DATE_1);
		assertThat(CollectionUtils.size(fuelLogList), is(1));
		assertThat(fuelLogList.get(0).getId(), notNullValue());
		assertThat(fuelLogList.get(0).getFuelPrice().getId(), notNullValue());
	}

	@Test
	void testUpdateFluelLog_willUpdateFuelLogAndFuelPrice() {
		var newTransactionType = FuelTransactionTypeEnum.FLIGHT;
		var newLeft = FULL_TANK;
		var newRight = FULL_TANK;
		var newChangeInLeft = FULL_TANK - 5f;
		var newChangeInRight = FULL_TANK - 4f;
		var newPrice = 2.5f;
		var newComment = "price drop";
		
		var fuelLogList = fuelLogRepository.findByRegistrationAndDate(REGISTRATION_1, DATE_1);
		var fuelLog = fuelLogList.get(0);
		fuelLog.setTransactionType(newTransactionType);
		fuelLog.setLeft(newLeft);
		fuelLog.setRight(newRight);
		fuelLog.setChangeInLeft(newChangeInLeft);
		fuelLog.setChangeInRight(newChangeInRight);
		var fuelLogVersion = fuelLog.getVersion();

		var fuelPrice = fuelLog.getFuelPrice();
		fuelPrice.setPricePerLitre(newPrice);
		fuelPrice.setComment(newComment);
		var fuelPriceVersion = fuelPrice.getVersion();
		fuelLogRepository.saveAndFlush(fuelLog);
		
		var fuelLogList2 = fuelLogRepository.findByRegistrationAndDate(REGISTRATION_1, DATE_1);
		assertThat(CollectionUtils.size(fuelLogList2), is(1));
		var updatedFuelLog = fuelLogList2.get(0);
		var updatedFuelPrice = updatedFuelLog.getFuelPrice();

		assertThat(updatedFuelLog.getTransactionType(), is(newTransactionType));
		assertThat(updatedFuelLog.getLeft(), is(newLeft));
		assertThat(updatedFuelLog.getRight(), is(newRight));
		assertThat(updatedFuelLog.getChangeInLeft(), is(newChangeInLeft));
		assertThat(updatedFuelLog.getChangeInRight(), is(newChangeInRight));
		assertThat(updatedFuelLog.getVersion(), is(fuelLogVersion+1));

		assertThat(updatedFuelPrice.getPricePerLitre(), is(newPrice));
		assertThat(updatedFuelPrice.getComment(), is(newComment));
		assertThat(updatedFuelPrice.getVersion(), is(fuelPriceVersion+1));
	}

	@Test
	void testDeleteFluelLog_willOnlyDeleteFuelLog() {
		var fuelLogList = fuelLogRepository.findByRegistrationAndDate(REGISTRATION_1, DATE_1);
		var fuelLog = fuelLogList.get(0);
		var fuelPriceId = fuelLog.getFuelPrice().getId();
		fuelLogRepository.delete(fuelLog);
		var fuelPriceOptional = fuelPriceRepository.findById(fuelPriceId);
		assertThat(fuelPriceOptional.isPresent(), is(true));
	}
}

