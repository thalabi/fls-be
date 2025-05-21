package com.kerneldc.fls.util.namedparameter;

import java.util.Set;

public record SetOfStringParam(String name, Set<String> value) implements NamedParameter {

	@Override
	public Class<?> getType() {
		return Set.class;
	}

}
