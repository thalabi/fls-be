package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.EntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.enginelog.EngineLog;

public interface EngineLogRepository extends BaseTableRepository<EngineLog, Long>{
	
	@Override
	default IEntityEnum canHandle() {
		return EntityEnum.ENGINE_LOG;
	}

}
