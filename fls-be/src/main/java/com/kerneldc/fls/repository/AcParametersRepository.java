package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.EntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.acparameters.AcParameters;

public interface AcParametersRepository extends BaseTableRepository<AcParameters, Long>{

	AcParameters findByRegistration(String registration);
	
	@Override
	default IEntityEnum canHandle() {
		return EntityEnum.AC_PARAMETERS;
	}

}
