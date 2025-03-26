package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.tsnv.TsnV;

public interface TsnVRepository extends BaseViewRepository<TsnV, Long>{

	TsnV findByRegistration(String registration);
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.TSN_V;
	}

}
