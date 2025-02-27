package com.kerneldc.fls.domain.fuellog;

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
public class FuelLog extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;
	
	@Setter(AccessLevel.NONE)
	private String registration;
	@Setter(AccessLevel.NONE)
	@Column(name = "\"date\"")
	private OffsetDateTime date;
	@Column(name = "\"left\"")
	private Float left;
	@Column(name = "\"right\"")
	private Float right;
	private Float changeInLeft;
	private Float changeInRight;
	private Float pricePerLitre;
	private String airport;
	private String fbo;
	private String comment;

	public void setRegistration(String registration) {
		this.registration = registration;
		setLogicalKeyHolder();
	}
	
	@Override
	protected void setLogicalKeyHolder() {
		var logicalKeyHolder = LogicalKeyHolder.build(registration, date);
		super.setLogicalKeyHolder(logicalKeyHolder);
	}

}
