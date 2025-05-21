package com.kerneldc.fls.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.service.HttpService.RequestTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AirportService {
	
	private final JwtTokenService jwtTokenService;
	private final HttpService httpService;
	
	private Set<String> identifierSet= new HashSet<>();
	
	public AirportService(
			JwtTokenService jwtTokenService,
			HttpService httpService) {
		this.jwtTokenService = jwtTokenService;
		this.httpService = httpService;
	}

	@SuppressWarnings("unchecked")
	public void loadIdentifiersFromExternalApi() throws ApplicationException  {
		var jwt = jwtTokenService.getJwtToken();
		var returnParams = httpService.processRequest(RequestTypeEnum.AIRPORT_IDENTIFIERS, jwt);
		identifierSet = returnParams.get("identifierSet", Set.class);
		LOGGER.info("Loaded [{}] airport identifiers", identifierSet.size());
	}

	public void refreshIdentifiersFromExternalApi() throws ApplicationException  {
		LOGGER.info("Clearing identifierSet");
		identifierSet.clear();
		
		loadIdentifiersFromExternalApi();
	}
	
	public Boolean isIdentifierValid(String identifier) throws ApplicationException {
		if (CollectionUtils.isEmpty(identifierSet)) {
			var message = "identifierSet is empty, possible error loading set from external api"; 
			LOGGER.error(message);
			throw new ApplicationException(message);
		}
		return identifierSet.contains(identifier);
	}

}
