package com.kerneldc.fls.domain.fuellog;

import java.time.OffsetDateTime;

import com.kerneldc.fls.domain.AbstractPersistableEntity;
import com.kerneldc.fls.domain.FuelTransactionTypeEnum;
import com.kerneldc.fls.domain.LogicalKeyHolder;
import com.kerneldc.fls.domain.fuelprice.FuelPrice;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class FuelLog extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;
	public static final String PROPERTY_FUEL_PRICE = "fuelPrice";
	
	@Setter(AccessLevel.NONE)
	private String registration;
	@Setter(AccessLevel.NONE)
	@Column(name = "\"date\"")
	private OffsetDateTime date;
	@Enumerated(EnumType.STRING)
	private FuelTransactionTypeEnum transactionType;
	@Column(name = "\"left\"")
	private Float left;
	@Column(name = "\"right\"")
	private Float right;
	private Float changeInLeft;
	private Float changeInRight;

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE /* all except delete */}, optional = true)
    @JoinColumn(name = "fuel_price_id")
	private FuelPrice fuelPrice;

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
