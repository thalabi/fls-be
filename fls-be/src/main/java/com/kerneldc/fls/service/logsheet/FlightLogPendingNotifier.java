package com.kerneldc.fls.service.logsheet;

import java.util.Set;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;

import com.kerneldc.fls.domain.remoteapicalllog.RemoteApiCallLog.RetryStatusEnum;
import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.repository.RemoteApiCallLogRepository;
import com.kerneldc.fls.service.AbstractRemoteApiCallBase;
import com.kerneldc.fls.service.HttpService;
import com.kerneldc.fls.service.HttpService.RequestTypeEnum;
import com.kerneldc.fls.service.JwtTokenService;
import com.kerneldc.fls.util.namedparameter.FloatParam;
import com.kerneldc.fls.util.namedparameter.NamedParameter;
import com.kerneldc.fls.util.namedparameter.StringParam;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FlightLogPendingNotifier extends AbstractRemoteApiCallBase {

	public FlightLogPendingNotifier(RemoteApiCallLogRepository remoteApiCallLogRepository,
			JwtTokenService jwtTokenService, HttpService httpService) {
		super(remoteApiCallLogRepository, jwtTokenService, httpService);
	}

	@Retryable(retryFor = ApplicationException.class,
	        maxAttemptsExpression = "${remote.api.call.retry.max.attempts:15}",
	        backoff =
	        	@Backoff(delayExpression = "${remote.api.call.retry.delay:30000}",
	        		multiplierExpression = "${remote.api.call.retry.multiplier:2}",
	        		maxDelayExpression = "${remote.api.call.retry.max.delay:491520000}") // retry after 30 sec, 1 min, 2 min, 4 min, ... 8192 min(5.68 days)
	)
//	listeners = {"loggingRetryListener"}
	public void addRemotely(LogSheetAddedEvent logSheetAddedEvent) throws ApplicationException {
		var remoteApiCall = logSheetAddedEvent.getRemoteApiCall();
		var retryCount = RetrySynchronizationManager.getContext().getRetryCount();
		var nextDelay = (retryCount != maxAttempts) ? delay * Math.pow(multiplier, retryCount) : 0;
		
		LOGGER.info("retryCount [{}] maxAttempts [{}], delay [{}], multiplier [{}] nextDelay [{}]", retryCount,
				maxAttempts, delay, multiplier, nextDelay);

		var flightLogPendingVo = logSheetAddedEvent.getFlightLogPendingVo();
		try {
			var jwt = jwtTokenService.getJwtToken();
			Set<NamedParameter> namedParameterSet = 
					Set.of(new StringParam("flightDate", flightLogPendingVo.flightDate()),
							new StringParam("routeFrom", flightLogPendingVo.routeFrom()),
							new StringParam("routeTo", flightLogPendingVo.routeTo()),
							new FloatParam("flightTime", flightLogPendingVo.flightTime()),
							new StringParam("registration", flightLogPendingVo.registration()),
							new StringParam("makeModel", flightLogPendingVo.makeModel()));
	
				httpService.processRequest(RequestTypeEnum.FLIGHT_LOG_PENDING_ADD, namedParameterSet, jwt);
				writeLog(remoteApiCall, retryCount + 1,
						(retryCount == 0 ? RetryStatusEnum.SUCCESS : RetryStatusEnum.RETRY_SUCCESS), null, 0);
			} catch (ApplicationException e) {
				writeLog(remoteApiCall, retryCount + 1, RetryStatusEnum.RETRY, e, nextDelay);
				LOGGER.error(e.getMessage());
				throw e;
			}
	}
	
	@Recover
	public void recover(Exception exception, LogSheetAddedEvent logSheetAddedEvent) {
		writeLog(logSheetAddedEvent.getRemoteApiCall(), 0, RetryStatusEnum.GIVE_UP, null, 0);
		LOGGER.error("FlightLogPendingNotifier failed");
	}
}
