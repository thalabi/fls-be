package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.EntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.remoteapicalllog.RemoteApiCallLog;

public interface RemoteApiCallLogRepository extends BaseTableRepository<RemoteApiCallLog, Long>{

	@Override
	default IEntityEnum canHandle() {
		return EntityEnum.REMOTE_API_CALL_LOG;
	}

}
