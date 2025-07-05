package com.kerneldc.fls.service.airport;

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
import com.kerneldc.fls.service.EmailService;
import com.kerneldc.fls.service.JwtTokenService;
import com.kerneldc.fls.service.http.HttpRequestTypeEnum;
import com.kerneldc.fls.service.http.HttpService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AirportIdentifierLoader extends AbstractRemoteApiCallBase {

	private final AirportService airportService;
	
	public AirportIdentifierLoader(RemoteApiCallLogRepository remoteApiCallLogRepository,
		JwtTokenService jwtTokenService, HttpService httpService, AirportService airportService, EmailService emailService) {
		
		super(remoteApiCallLogRepository, jwtTokenService, httpService, emailService);
		
		this.airportService = airportService;
	}

	@Retryable(retryFor = ApplicationException.class,
	        maxAttemptsExpression = "${remote.api.call.retry.max.attempts}",
	        backoff =
	        	@Backoff(delayExpression = "${remote.api.call.retry.delay}",
	        		multiplierExpression = "${remote.api.call.retry.multiplier}",
	        		maxDelayExpression = "${remote.api.call.retry.max.delay}") // retry after 30 sec, 1 min, 2 min, 4 min, ... 8192 min(5.68 days)
	)
//	listeners = {"loggingRetryListener"}
	public void loadRemotely(LoadIdentifiersEvent loadIdentifiersEvent) throws ApplicationException {
		var remoteApiCall = loadIdentifiersEvent.getRemoteApiCall();
		var retryCount = RetrySynchronizationManager.getContext().getRetryCount();
		var nextDelay = delay * Math.pow(multiplier, retryCount);
		
		LOGGER.info("retryCount [{}] maxAttempts [{}], delay [{}], multiplier [{}] nextDelay [{}]", retryCount,
				maxAttempts, delay, multiplier, nextDelay);
		
		try {
			var jwt = jwtTokenService.getJwtToken();
			var returnParams = httpService.processRequest(HttpRequestTypeEnum.AIRPORT_IDENTIFIERS, jwt);
			@SuppressWarnings("unchecked")
			Set<String> identifierSet = returnParams.get("identifierSet", Set.class);
			airportService.setIdentifierSet(identifierSet);
			LOGGER.info("Loaded [{}] airport identifiers", identifierSet.size());
			writeLog(remoteApiCall, retryCount + 1,
						(retryCount == 0 ? RetryStatusEnum.SUCCESS : RetryStatusEnum.RETRY_SUCCESS), null, 0);
			// send success email if call succeeded after retrying
			if (retryCount != 0) {
				LOGGER.info("Sending success after retrying email");
				emailService.sendRemoteApiSuccessAfterRetryEmail(HttpRequestTypeEnum.AIRPORT_IDENTIFIERS, retryCount);
			}
		} catch (ApplicationException applicationException) {
			writeLog(remoteApiCall, retryCount + 1, RetryStatusEnum.RETRY, applicationException, nextDelay);
			LOGGER.error(applicationException.getMessage());
			// send failure email on failure
			if (retryCount == 0) { // first time call fails
				LOGGER.info("Sending failure email");
				emailService.sendRemoteApiFailureEmail(HttpRequestTypeEnum.AIRPORT_IDENTIFIERS, applicationException);
			}
			throw applicationException;
		}
	}
	
	@Recover
	public void recover(Exception exception, LoadIdentifiersEvent loadIdentifiersEvent) {
		writeLog(loadIdentifiersEvent.getRemoteApiCall(), 0, RetryStatusEnum.GIVE_UP, null, 0);
		LOGGER.error("FlightLogPendingNotifier failed");
	}
}
