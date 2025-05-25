package com.kerneldc.fls.service;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;

import com.kerneldc.fls.domain.remoteapicall.RemoteApiCall;
import com.kerneldc.fls.domain.remoteapicalllog.RemoteApiCallLog;
import com.kerneldc.fls.domain.remoteapicalllog.RemoteApiCallLog.RetryStatusEnum;
import com.kerneldc.fls.repository.RemoteApiCallLogRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRemoteApiCallBase {

	protected final RemoteApiCallLogRepository remoteApiCallLogRepository;
	protected final JwtTokenService jwtTokenService;
	protected final HttpService httpService;
	
	@Value("${remote.api.call.retry.max.attempts:5}")
	protected int maxAttempts;
	@Value("${remote.api.call.retry.delay:30000}")
	protected long delay;
	@Value("${remote.api.call.retry.multiplier:2}")
	protected int multiplier;

	protected void writeLog(RemoteApiCall remoteApiCall, int attempt, RetryStatusEnum status, Exception exception, double nextDelay) {
		var now = OffsetDateTime.now();
		var remoteApiCallLog = new RemoteApiCallLog();
		remoteApiCallLog.setRemoteApiCall(remoteApiCall);
		if (attempt != 0) {
			remoteApiCallLog.setAttempt(attempt);
		}
		remoteApiCallLog.setStatus(status);
		if (exception != null) {
			remoteApiCallLog.setMessage(exception.getMessage());
			remoteApiCallLog.setStackTrace(ExceptionUtils.getStackTrace(exception));
		}
		remoteApiCallLog.setTimestamp(now);
		if (nextDelay != 0) {
			remoteApiCallLog.setNextRetryTime(now.plus(Duration.ofMillis((long)nextDelay)));
		}
		remoteApiCallLogRepository.save(remoteApiCallLog);
	}

}
