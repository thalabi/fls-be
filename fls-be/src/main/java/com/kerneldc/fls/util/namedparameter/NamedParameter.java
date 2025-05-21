package com.kerneldc.fls.util.namedparameter;

public sealed interface NamedParameter permits StringParam, BooleanParam, IntegerParam, FloatParam, DateParam,
		LocalDateTimeParam, LongParam, SetOfStringParam {

	String name();
	
    Object value();

    default String getName() {
        return name();
    }

    default Object getValue() {
        return value();
    }
    
	Class<?> getType();
	
	

}
