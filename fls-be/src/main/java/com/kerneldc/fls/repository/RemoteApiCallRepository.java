package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.remoteapicall.RemoteApiCall;

public interface RemoteApiCallRepository extends BaseTableRepository<RemoteApiCall, Long>{

	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.REMOTE_API_CALL_DETAIL;
	}

}
