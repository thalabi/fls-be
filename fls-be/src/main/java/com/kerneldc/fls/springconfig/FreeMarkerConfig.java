package com.kerneldc.fls.springconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FreeMarkerConfig {

	@Bean
	public FreeMarkerConfigurationFactoryBean getFreeMarkerConfigurationFactoryBean() {
		return new FreeMarkerConfigurationFactoryBean();
	}
}
