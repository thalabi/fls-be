package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.tsmoh.TsmohV;

public interface TsmohVRepository extends BaseViewRepository<TsmohV, Long>{

	TsmohV findByRegistration(String registration);
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.TSMOH_V;
	}

}
