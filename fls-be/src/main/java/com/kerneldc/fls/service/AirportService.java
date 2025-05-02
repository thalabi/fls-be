package com.kerneldc.fls.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.fls.exeption.LoadingFromExternalApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AirportService {
	
	private final boolean urlLoggingEnabled;
	private final String airportServiceUrl;
	
	private Set<String> identifierSet;
	
	public AirportService(@Value("${httputil.url.logging.enabled:false}") boolean urlLoggingEnabled, @Value("${airport.service.url}") String airportServiceUrl) {
		this.urlLoggingEnabled = urlLoggingEnabled;
		this.airportServiceUrl = airportServiceUrl; 
	}

	public void loadIdentifiersFromExternalApi() throws LoadingFromExternalApiException  {
		LOGGER.info("Loading airport identifiers from external api [{}]", airportServiceUrl);

		var jsonResponse = getUrlContent(airportServiceUrl);
		var mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(jsonResponse);
		} catch (JsonProcessingException e) {
			var message = String.format("Exception when parsing json response: %s", jsonResponse);
			LOGGER.error(message, e);
			throw new LoadingFromExternalApiException(message);
		}
		LOGGER.debug(root.toPrettyString());
		var identifiers = root.path("identifiers");
		if (identifiers.isArray()) {
			identifierSet = new HashSet<>();
			for (JsonNode identifier : identifiers) {
				identifierSet.add(identifier.asText());
			}
			LOGGER.info("Loaded [{}] airport identifiers", identifierSet.size());
		}
	}

	
	public Boolean isIdentifierValid(String identifier) throws LoadingFromExternalApiException {
		if (CollectionUtils.isEmpty(identifierSet)) {
			var message = "identifierSet is empty, possible error loading set from external api"; 
			LOGGER.error(message);
			throw new LoadingFromExternalApiException(message);
		}
		return identifierSet.contains(identifier);
	}
	
	private String getUrlContent(String urlString) throws LoadingFromExternalApiException {
		HttpURLConnection httpUrlConnection;
		try {
			if (urlLoggingEnabled) LOGGER.info("Hitting url: [{}]", urlString);
			var url = new URI(urlString).toURL();
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			var httpStatusCode = httpUrlConnection.getResponseCode();
			if (httpStatusCode != HttpURLConnection.HTTP_OK) {
				var message = String.format("Fetching contents of URL [%s], returned Http status code [%d]. ",
						url.toString(), httpStatusCode);
				var urlContent = IOUtils.toString(httpUrlConnection.getErrorStream(), Encoding.DEFAULT_CHARSET);
				if (StringUtils.isNotEmpty(urlContent)) {
					message += String.format("Site message: [%s]", urlContent);
				}
				throw new LoadingFromExternalApiException(message);
			}
			return IOUtils.toString(httpUrlConnection.getInputStream(), Encoding.DEFAULT_CHARSET);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			var message = String.format("Fetching contents of URL [%s]failed. ", urlString);
			throw new LoadingFromExternalApiException(message, e);
		}
	}

}
