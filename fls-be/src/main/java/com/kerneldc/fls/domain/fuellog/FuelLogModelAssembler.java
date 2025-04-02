package com.kerneldc.fls.domain.fuellog;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.stereotype.Component;

import com.kerneldc.fls.domain.AbstractEntity;
import com.kerneldc.fls.domain.AbstractEntityModel;
import com.kerneldc.fls.domain.IComplexEntityRepresentationModelAssembler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FuelLogModelAssembler implements IComplexEntityRepresentationModelAssembler {

	private final RepositoryEntityLinks repositoryEntityLinks;

	@Override
	public AbstractEntityModel toModel(AbstractEntity entity) {
		LinkBuilder fuelLogLinkBuilder = repositoryEntityLinks.linkForItemResource(entity, AbstractEntity.idExtractor);
		FuelLog fuelLog = (FuelLog)entity;
		FuelLogModel fuelLogModel = new FuelLogModel();
		fuelLogModel.setFuelLog(fuelLog);
		
		fuelLogModel.setFuelPrice(fuelLog.getFuelPrice());
		
		fuelLogModel.add(fuelLogLinkBuilder.withSelfRel());
		fuelLogModel.add(fuelLogLinkBuilder.withRel(FuelLog.class.getSimpleName().toLowerCase()));
		fuelLogModel.add(fuelLogLinkBuilder.slash(FuelLog.PROPERTY_FUEL_PRICE).withRel(FuelLog.PROPERTY_FUEL_PRICE));
		return fuelLogModel;
	}

	@Override
	public boolean canHandle(Class<? extends AbstractEntity> entityType) {
		return entityType.equals(FuelLog.class);
	}

}
