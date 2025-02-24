package com.kerneldc.fls.domain;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
@Relation(collectionRelation = "simpleModels")
public class AbstractEntityModel extends RepresentationModel<AbstractEntityModel> {

	@JsonUnwrapped
	private AbstractEntity abstractEntity;

}
