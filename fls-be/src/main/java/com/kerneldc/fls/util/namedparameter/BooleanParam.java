package com.kerneldc.fls.util.namedparameter;

public record BooleanParam(String name, Boolean value) implements NamedParameter {

	@Override
	public Class<?> getType() {
		return Boolean.class;
	}

}
