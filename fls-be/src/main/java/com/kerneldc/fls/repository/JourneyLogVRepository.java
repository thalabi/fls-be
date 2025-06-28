package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.EntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.journeylogv.JourneyLogV;

public interface JourneyLogVRepository extends BaseViewRepository<JourneyLogV, Long>{

	JourneyLogV findByRegistration(String registration);
	
	@Override
	default IEntityEnum canHandle() {
		return EntityEnum.JOURNEY_LOG_V;
	}

}
