package com.kerneldc.fls.domain.logsheet;

import java.time.OffsetDateTime;

import com.kerneldc.fls.domain.AbstractPersistableEntity;
import com.kerneldc.fls.domain.LogicalKeyHolder;
import com.kerneldc.fls.domain.enginelog.EngineLog;
import com.kerneldc.fls.domain.journeylog.JourneyLog;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class LogSheet extends AbstractPersistableEntity {

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
	private OffsetDateTime takeoffTime;
	private OffsetDateTime landingTime;
	private Float airtime;
	private Float flightTime;

	@OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "journey_log_id")
	private JourneyLog journeyLog;
	@OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "engine_log_id")
	private EngineLog engineLog;
	
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
