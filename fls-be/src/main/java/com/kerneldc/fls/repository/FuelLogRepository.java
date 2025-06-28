package com.kerneldc.fls.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kerneldc.fls.domain.EntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.fuellog.FuelLog;

public interface FuelLogRepository extends BaseTableRepository<FuelLog, Long>{

	List<FuelLog> findTopByRegistrationOrderByDateDesc(String registration);
	List<FuelLog> findByRegistrationAndDate(String registration, OffsetDateTime date);
	
	@Modifying
	@Query(value = "delete from fuel_log where registration = :registration", nativeQuery = true)
	void deleteInBulkByRegistration(String registration);
	
	@Override
	default IEntityEnum canHandle() {
		return EntityEnum.FUEL_LOG;
	}


}
