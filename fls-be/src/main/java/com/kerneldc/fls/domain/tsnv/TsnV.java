package com.kerneldc.fls.domain.tsnv;

import org.hibernate.annotations.Immutable;

import com.kerneldc.fls.domain.AbstractImmutableEntity;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity(name = "tsn_v")
@Immutable
@Getter
public class TsnV extends AbstractImmutableEntity {

	private String registration;
	private Float tsn;
}
