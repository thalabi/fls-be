package com.kerneldc.fls.domain;

public interface IEntityEnum {

	Class<? extends AbstractEntity> getEntity();
	boolean isImmutable();
	String[] getWriteColumnOrder();
}