package com.kerneldc.fls.repository;

import java.time.OffsetDateTime;
import java.util.List;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.fuellog.FuelLog;

public interface FuelLogRepository extends BaseTableRepository<FuelLog, Long>{

	List<FuelLog> findTopByRegistrationOrderByDateDesc(String registration);
	List<FuelLog> findByRegistrationAndDate(String registration, OffsetDateTime date);
	
	void deleteByRegistrationAndDateBefore(String registration, OffsetDateTime date);
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.FUEL_LOG;
	}


}
