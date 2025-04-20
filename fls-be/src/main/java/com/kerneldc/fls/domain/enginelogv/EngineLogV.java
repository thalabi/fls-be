package com.kerneldc.fls.domain.enginelogv;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Immutable;

import com.kerneldc.fls.domain.AbstractImmutableEntity;
import com.kerneldc.fls.domain.EnginePositionEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Entity(name = "engine_log_v")
@Immutable
@Getter
public class EngineLogV extends AbstractImmutableEntity {

	private String registration;
	@Enumerated(EnumType.STRING)
	private EnginePositionEnum position;
	@Column(name = "\"date\"")
	private OffsetDateTime date;
	private Float airtime;
	private String comment;
	private Float tsmoh;
}
