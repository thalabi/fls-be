package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.EntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.logsheet.LogSheet;

public interface LogSheetRepository extends BaseTableRepository<LogSheet, Long>{
	
	@Override
	default IEntityEnum canHandle() {
		return EntityEnum.LOG_SHEET;
	}

}
