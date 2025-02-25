package com.kerneldc.fls.springconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class HttpLogging {

	//@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(true);
		filter.setBeforeMessagePrefix("HTTP Message before processing - ");
		filter.setAfterMessagePrefix("HTTP Message after processing - ");
		return filter;
	}

}
