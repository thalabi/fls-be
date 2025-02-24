package com.kerneldc.fls.domain.acparameters;

import com.kerneldc.fls.domain.AbstractPersistableEntity;
import com.kerneldc.fls.domain.LogicalKeyHolder;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class AcParameters extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;
	
	@Setter(AccessLevel.NONE)	private String registration;
	private Float initialTtsn;
	private Float initialTsmoh;
	private Float eachTankCapacity;
	private Float fuelBurn;

	public void setRegistration(String registration) {
		this.registration = registration;
		setLogicalKeyHolder();
	}
	
	@Override
	protected void setLogicalKeyHolder() {
		var logicalKeyHolder = LogicalKeyHolder.build(registration);
		super.setLogicalKeyHolder(logicalKeyHolder);
	}

}
