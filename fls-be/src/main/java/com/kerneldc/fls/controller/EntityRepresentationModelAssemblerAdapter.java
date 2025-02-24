package com.kerneldc.fls.controller;

import java.util.Collection;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import com.kerneldc.fls.domain.AbstractEntity;
import com.kerneldc.fls.domain.AbstractEntityModel;
import com.kerneldc.fls.domain.IComplexEntityRepresentationModelAssembler;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Component
@Slf4j
public class EntityRepresentationModelAssemblerAdapter extends RepresentationModelAssemblerSupport<AbstractEntity, AbstractEntityModel> {

	private final RepositoryEntityLinks repositoryEntityLinks;
	private final Collection<IComplexEntityRepresentationModelAssembler> iComplexEntityRepresentationModelAssemblers;
	
	public EntityRepresentationModelAssemblerAdapter(RepositoryEntityLinks repositoryEntityLinks, Collection<IComplexEntityRepresentationModelAssembler> complexEntityRepresentationModelAssemblerss) {
		super(GenericEntityController.class, AbstractEntityModel.class);
		
		this.repositoryEntityLinks = repositoryEntityLinks;
		this.iComplexEntityRepresentationModelAssemblers = complexEntityRepresentationModelAssemblerss;
		
		LOGGER.info("Loading complex entities model assemblers:");
		for (IComplexEntityRepresentationModelAssembler iComplexEntityRepresentationModelAssembler: complexEntityRepresentationModelAssemblerss) {
			
			LOGGER.info("[{}]", iComplexEntityRepresentationModelAssembler.getClass().getSimpleName());
		}
	}

	@Override
	public AbstractEntityModel toModel(AbstractEntity entity) {
		
		// Handle a complex entity
		for (IComplexEntityRepresentationModelAssembler iComplexEntityRepresentationModelAssembler: iComplexEntityRepresentationModelAssemblers) {
			
			if (iComplexEntityRepresentationModelAssembler.canHandle(entity.getClass())) {
				
				return complexEntityToModel(entity, iComplexEntityRepresentationModelAssembler);
			}
			
		}
		
		// Otherwise handle a simple entity
		return simpleEntityToModel(entity);
	}

	private AbstractEntityModel simpleEntityToModel(AbstractEntity entity) {
		
		LOGGER.info("handling simple entity: [{}]", entity.getClass().getSimpleName());
		AbstractEntityModel model = new AbstractEntityModel();
		model.setAbstractEntity(entity);
		Link link = repositoryEntityLinks.linkToItemResource(entity, AbstractEntity.idExtractor);
		model.add(link);
		model.add(link.withSelfRel());
		
		return model;
	}

	private AbstractEntityModel complexEntityToModel(AbstractEntity entity,
			IComplexEntityRepresentationModelAssembler iComplexEntityRepresentationModelAssembler) {
		
		LOGGER.info("handling complex entity: [{}]", entity.getClass().getSimpleName());
		return iComplexEntityRepresentationModelAssembler.toModel(entity);
	}
}
