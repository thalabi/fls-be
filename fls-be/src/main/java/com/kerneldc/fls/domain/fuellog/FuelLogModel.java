package com.kerneldc.fls.domain.fuellog;

import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kerneldc.fls.domain.AbstractEntityModel;
import com.kerneldc.fls.domain.fuelprice.FuelPrice;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Relation(collectionRelation = "fuelLogModels")
public class FuelLogModel extends AbstractEntityModel {

	@JsonUnwrapped
	private FuelLog fuelLog;
	
	private FuelPrice fuelPrice;
}
