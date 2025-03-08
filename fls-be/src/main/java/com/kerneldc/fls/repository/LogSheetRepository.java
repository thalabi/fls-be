package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.logsheet.LogSheet;

public interface LogSheetRepository extends BaseTableRepository<LogSheet, Long>{
	
	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.LOG_SHEET;
	}

}
