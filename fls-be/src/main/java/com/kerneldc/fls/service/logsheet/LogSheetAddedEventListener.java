package com.kerneldc.fls.service.logsheet;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.kerneldc.fls.exeption.ApplicationException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogSheetAddedEventListener {

	private final FlightLogPendingNotifier flightLogPendingNotifier;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onLogSheetAdded(LogSheetAddedEvent logSheetAddedEvent) throws ApplicationException {
		flightLogPendingNotifier.addRemotely(logSheetAddedEvent);
	}
}
