package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.EntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.remoteapicall.RemoteApiCall;

public interface RemoteApiCallRepository extends BaseTableRepository<RemoteApiCall, Long>{

	@Override
	default IEntityEnum canHandle() {
		return EntityEnum.REMOTE_API_CALL_DETAIL;
	}

}
