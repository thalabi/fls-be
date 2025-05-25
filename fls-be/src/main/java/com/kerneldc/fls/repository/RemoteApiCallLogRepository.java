package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.remoteapicalllog.RemoteApiCallLog;

public interface RemoteApiCallLogRepository extends BaseTableRepository<RemoteApiCallLog, Long>{

	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.REMOTE_API_CALL_LOG;
	}

}
