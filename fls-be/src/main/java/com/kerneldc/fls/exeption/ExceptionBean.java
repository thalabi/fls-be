package com.kerneldc.fls.exeption;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ExceptionBean {

	private String message;
	private String stackTrace;
	private Integer oracleSqlError;
	private String oracleSqlMessage;
	private String originalSqlStatement;
	private String sqlStatement;
}
