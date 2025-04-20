package com.kerneldc.fls.domain.journeylogv;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Immutable;

import com.kerneldc.fls.domain.AbstractImmutableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity(name = "journey_log_v")
@Immutable
@Getter
public class JourneyLogV extends AbstractImmutableEntity {

	private String registration;
	@Column(name = "\"date\"")
	private OffsetDateTime date;
	@Column(name = "\"from\"")
	private String from;
	@Column(name = "\"to\"")
	private String to;
	private OffsetDateTime takeoffTime;
	private OffsetDateTime landingTime;
	private Float airtime;
	private String comment;
	private Float tsn;
}
