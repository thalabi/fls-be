package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.journeylog.JourneyLog;

public interface JourneyLogRepository extends BaseTableRepository<JourneyLog, Long>{
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.JOURNEY_LOG;
	}

}
