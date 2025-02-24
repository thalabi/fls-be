package com.kerneldc.fls.domain;

import java.util.Arrays;

import com.kerneldc.fls.domain.acparameters.AcParameters;

public enum FlsEntityEnum implements IEntityEnum {
	AC_PARAMETERS(AcParameters.class, false, new String[] {"NAME","TICKER","EXCHANGE","CURRENCY"}),
	;

	Class<? extends AbstractEntity> entity;
	boolean immutable;
	String[] writeColumnOrder;

	FlsEntityEnum(Class<? extends AbstractEntity> entity, boolean immutable) {
		this.entity = entity;
		this.immutable = immutable;
	}
	FlsEntityEnum(Class<? extends AbstractEntity> entity, boolean immutable, String[] writeColumnOrder) {
		this.entity = entity;
		this.immutable = immutable;
		// tag SOURCECSVLINENUMBER to the end of the writeColumnOrder
		this.writeColumnOrder = Arrays.copyOf(writeColumnOrder, writeColumnOrder.length+1);
		this.writeColumnOrder[this.writeColumnOrder.length-1] = "SOURCECSVLINENUMBER";  
	}

	@Override
	public Class<? extends AbstractEntity> getEntity() {
		return entity;
	}

	@Override
	public boolean isImmutable() {
		return immutable;
	}

	@Override
	public String[] getWriteColumnOrder() {
		return writeColumnOrder;
	}

}
