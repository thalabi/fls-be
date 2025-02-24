package com.kerneldc.fls.repository;

import java.util.List;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.acparameters.AcParameters;

public interface AcParametersRepository extends BaseTableRepository<AcParameters, Long>{

	List<AcParameters> findAllByOrderByRegistration();
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.AC_PARAMETERS;
	}

}
