package com.kerneldc.fls.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kerneldc.fls.domain.AbstractEntity;
import com.kerneldc.fls.domain.AbstractEntityModel;
import com.kerneldc.fls.domain.EntityEnumUtilities;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.repository.EntityRepositoryFactory;
import com.kerneldc.fls.search.EntitySpecification;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/protected/genericEntityController")
@RequiredArgsConstructor
@Slf4j
public class GenericEntityController {

	private final EntityRepositoryFactory<AbstractEntity, Long> entityRepositoryFactory;
	private final EntityRepresentationModelAssemblerAdapter entityRepresentationModelAssemblerAdapter;
	private final EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@GetMapping("/findAll")
	public ResponseEntity<PagedModel<AbstractEntityModel>> findAll(
			@RequestParam @NotBlank String tableName, @RequestParam String search,
			Pageable pageable, @NotNull PagedResourcesAssembler<AbstractEntity> pagedResourcesAssembler) {

		IEntityEnum entityEnum = EntityEnumUtilities.getEntityEnum(tableName);
    	LOGGER.info("search: {}", search);
    	LOGGER.info("pageable: {}", pageable);
    	
    	// Retrieve the repository with proper typing
    	var entityRepository = entityRepositoryFactory.getRepository(entityEnum);
    	
    	// Retrieve entity metamodel with proper typing
    	var entityMetamodel = entityManager.getMetamodel().entity(entityEnum.getEntity());
    	
    	// Create a typed specification
    	var entitySpecification = new EntitySpecification<AbstractEntity>(entityMetamodel, search);
    	
    	// Perform the query
		var page = entityRepository.findAll(entitySpecification, pageable);
		
		// Build the PagedModel
        PagedModel<AbstractEntityModel> pagedModel;
        
        if (! /* not */ page.hasContent()) {
        	pagedModel = (PagedModel<AbstractEntityModel>) pagedResourcesAssembler.toEmptyModel(page, entityEnum.getEntity());
        } else {
        	var link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GenericEntityController.class).findAll(tableName, search, pageable, pagedResourcesAssembler)).withSelfRel();
        	pagedModel = pagedResourcesAssembler.toModel(page, entityRepresentationModelAssemblerAdapter, link);
        }

		LOGGER.debug("pagedModel: {}", pagedModel);
		
		var response = ResponseEntity.ok(pagedModel);
		
		LOGGER.debug("r: {}", response);
		return response;	
    }

	@GetMapping("/countAll")
	public ResponseEntity<Long> countAll(@RequestParam @NotBlank String tableName) {

		IEntityEnum entityEnum = EntityEnumUtilities.getEntityEnum(tableName);
		var entityRepository = entityRepositoryFactory.getRepository(entityEnum);

		var count = entityRepository.count();
		var response = ResponseEntity.ok(count);

		LOGGER.debug("r: {}", response);
		return response;
	}
}
