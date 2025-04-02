package com.kerneldc.fls.domain.fuelprice;

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
public class FuelPrice extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;
	
	@Setter(AccessLevel.NONE)
	private String airport;
	@Setter(AccessLevel.NONE)
	private String fbo;
	@Setter(AccessLevel.NONE)
	@Column(name = "\"date\"")
	private OffsetDateTime date;

	private Float pricePerLitre;
	private String comment;

	public void setAirport(String airport) {
		this.airport = airport;
		setLogicalKeyHolder();
	}
	public void setFbo(String fbo) {
		this.fbo = fbo;
		setLogicalKeyHolder();
	}
	public void setDate(OffsetDateTime date) {
		this.date = date;
		setLogicalKeyHolder();
	}
	
	@Override
	protected void setLogicalKeyHolder() {
		var logicalKeyHolder = LogicalKeyHolder.build(airport, fbo, date);
		super.setLogicalKeyHolder(logicalKeyHolder);
	}


}
