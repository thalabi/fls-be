package com.kerneldc.fls.util.namedparameter;

public record IntegerParam(String name, Integer value) implements NamedParameter {

	@Override
	public Class<?> getType() {
		return Integer.class;
	}

}
