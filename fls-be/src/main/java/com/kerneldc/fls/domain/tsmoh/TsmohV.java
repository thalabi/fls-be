package com.kerneldc.fls.domain.tsmoh;

import org.hibernate.annotations.Immutable;

import com.kerneldc.fls.domain.AbstractImmutableEntity;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity(name = "tsmoh_v")
@Immutable
@Getter
public class TsmohV extends AbstractImmutableEntity {

	private String registration;
	private Float tsmoh;
}
