package com.kerneldc.fls.repository;

import java.util.List;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.fuellog.FuelLog;

public interface FuelLogRepository extends BaseTableRepository<FuelLog, Long>{

	//List<FuelLog> findAllByOrderByRegistrationAscDateAsc();
	List<FuelLog> findTopByRegistrationOrderByDateDesc(String registration);
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.FUEL_LOG;
	}

}
