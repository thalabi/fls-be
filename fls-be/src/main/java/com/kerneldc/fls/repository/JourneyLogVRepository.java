package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.journeylogv.JourneyLogV;

public interface JourneyLogVRepository extends BaseViewRepository<JourneyLogV, Long>{

	JourneyLogV findByRegistration(String registration);
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.JOURNEY_LOG_V;
	}

}
