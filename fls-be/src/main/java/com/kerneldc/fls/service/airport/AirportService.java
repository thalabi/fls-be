package com.kerneldc.fls.service.airport;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.kerneldc.fls.domain.remoteapicall.RemoteApiCall;
import com.kerneldc.fls.domain.remoteapicalllog.RemoteApiCallLog;
import com.kerneldc.fls.domain.remoteapicalllog.RemoteApiCallLog.RetryStatusEnum;
import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.repository.RemoteApiCallLogRepository;
import com.kerneldc.fls.repository.RemoteApiCallRepository;
import com.kerneldc.fls.service.HttpService.RequestTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirportService {
	
	private final RemoteApiCallLogRepository remoteApiCallLogRepository;
	private final RemoteApiCallRepository remoteApiCallRepository;
	private final ApplicationEventPublisher eventPublisher;
	
	@Setter
	private Set<String> identifierSet = new HashSet<>();
	
	public void loadIdentifiersFromExternalApi() {

		// publish event to trigger remote api call
		var now = OffsetDateTime.now();
		
		var remoteApiCall = new RemoteApiCall();
		remoteApiCall.setRequest(RequestTypeEnum.AIRPORT_IDENTIFIERS);
		remoteApiCall.setTimestamp(now);
		remoteApiCallRepository.save(remoteApiCall);
		
		var remoteApiCallLog = new RemoteApiCallLog();
		remoteApiCallLog.setStatus(RetryStatusEnum.NEVER_ATTEMPTED);
		remoteApiCallLog.setRemoteApiCall(remoteApiCall);
		remoteApiCallLog.setTimestamp(now);
		remoteApiCallLogRepository.save(remoteApiCallLog);
		
		var loadIdentifiersEvent = new LoadIdentifiersEvent(this, remoteApiCall);
		eventPublisher.publishEvent(loadIdentifiersEvent);
	}

	public void refreshIdentifiersFromExternalApi() throws ApplicationException  {
		LOGGER.info("Clearing identifierSet");
		identifierSet.clear();
		
		loadIdentifiersFromExternalApi();
	}
	
	public Boolean isIdentifierValid(String identifier) throws ApplicationException {
		if (CollectionUtils.isEmpty(identifierSet)) {
			var message = "identifierSet is empty, possible error loading set from external api"; 
			LOGGER.error(message);
			throw new ApplicationException(message);
		}
		return identifierSet.contains(identifier);
	}

}
