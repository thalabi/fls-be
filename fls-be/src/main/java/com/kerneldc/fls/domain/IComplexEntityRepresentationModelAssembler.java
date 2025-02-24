package com.kerneldc.fls.domain;

import org.springframework.hateoas.server.RepresentationModelAssembler;

public interface IComplexEntityRepresentationModelAssembler extends RepresentationModelAssembler<AbstractEntity, AbstractEntityModel> {

	@Override
	AbstractEntityModel toModel(AbstractEntity entity);
	
	boolean canHandle(Class<? extends AbstractEntity> entityType);
}
