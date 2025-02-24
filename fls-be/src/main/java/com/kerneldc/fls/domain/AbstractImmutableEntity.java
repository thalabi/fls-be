package com.kerneldc.fls.domain;


import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter @Setter
public abstract class AbstractImmutableEntity extends AbstractEntity {
	
	@Id
	private Long id;

}
