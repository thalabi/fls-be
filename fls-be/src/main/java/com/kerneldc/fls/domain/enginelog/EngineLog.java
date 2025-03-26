package com.kerneldc.fls.domain.enginelog;

import java.time.OffsetDateTime;

import com.kerneldc.fls.domain.AbstractPersistableEntity;
import com.kerneldc.fls.domain.EnginePositionEnum;
import com.kerneldc.fls.domain.LogicalKeyHolder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class EngineLog extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;
	
	@Setter(AccessLevel.NONE)
	private String registration;
	@Setter(AccessLevel.NONE)
	@Enumerated(EnumType.STRING)
	private EnginePositionEnum position;
	@Setter(AccessLevel.NONE)
	@Column(name = "\"date\"")
	private OffsetDateTime date;
	private Float airtime;
	private String comment;

	public void setRegistration(String registration) {
		this.registration = registration;
		setLogicalKeyHolder();
	}
	
	public void setPosition(EnginePositionEnum position) {
		this.position = position;
		setLogicalKeyHolder();
	}

	public void setDate(OffsetDateTime date) {
		this.date = date;
		setLogicalKeyHolder();
	}
	
	@Override
	protected void setLogicalKeyHolder() {
		var logicalKeyHolder = LogicalKeyHolder.build(registration, position, date);
		super.setLogicalKeyHolder(logicalKeyHolder);
	}

}
