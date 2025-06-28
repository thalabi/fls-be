package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.EntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.enginelogv.EngineLogV;

public interface EngineLogVRepository extends BaseViewRepository<EngineLogV, Long>{

	EngineLogV findByRegistration(String registration);
	
	@Override
	default IEntityEnum canHandle() {
		return EntityEnum.ENGINE_LOG_V;
	}

}
