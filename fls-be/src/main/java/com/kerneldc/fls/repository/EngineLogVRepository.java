package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.enginelogv.EngineLogV;

public interface EngineLogVRepository extends BaseViewRepository<EngineLogV, Long>{

	EngineLogV findByRegistration(String registration);
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.ENGINE_LOG_V;
	}

}
