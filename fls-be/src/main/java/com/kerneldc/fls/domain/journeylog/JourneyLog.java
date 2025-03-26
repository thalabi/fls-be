package com.kerneldc.fls.domain.journeylog;

import java.time.OffsetDateTime;

import com.kerneldc.fls.domain.AbstractPersistableEntity;
import com.kerneldc.fls.domain.LogicalKeyHolder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class JourneyLog extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;
	
	@Setter(AccessLevel.NONE)
	private String registration;
	@Setter(AccessLevel.NONE)
	@Column(name = "\"date\"")
	private OffsetDateTime date;
	@Column(name = "\"from\"")
	private String from;
	@Column(name = "\"to\"")
	private String to;
	private Float airtime;
	private String comment;

	public void setRegistration(String registration) {
		this.registration = registration;
		setLogicalKeyHolder();
	}
	
	public void setDate(OffsetDateTime date) {
		this.date = date;
		setLogicalKeyHolder();
	}
	
	@Override
	protected void setLogicalKeyHolder() {
		var logicalKeyHolder = LogicalKeyHolder.build(registration, date);
		super.setLogicalKeyHolder(logicalKeyHolder);
	}

}
