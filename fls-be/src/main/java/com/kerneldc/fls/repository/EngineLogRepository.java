package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.enginelog.EngineLog;

public interface EngineLogRepository extends BaseTableRepository<EngineLog, Long>{
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.ENGINE_LOG;
	}

}
