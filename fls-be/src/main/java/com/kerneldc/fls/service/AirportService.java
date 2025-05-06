package com.kerneldc.fls.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
	private final String airportServiceJwtServerUrl;
	private final String airportServiceJwtClientId;
	private final String airportServiceJwtClientSecret;
	
	private Set<String> identifierSet= new HashSet<>();
	
	public AirportService(@Value("${httputil.url.logging.enabled:false}") boolean urlLoggingEnabled,
			@Value("${airport.service.url}") String airportServiceUrl,
			@Value("${airport.service.jwt.server.url}") String airportServiceJwtServerUrl,
			@Value("${airport.service.jwt.client.id}") String airportServiceJwtClientId,
			@Value("${airport.service.jwt.client.secret}") String airportServiceJwtClientSecret) {
		this.urlLoggingEnabled = urlLoggingEnabled;
		this.airportServiceUrl = airportServiceUrl;
		this.airportServiceJwtServerUrl = airportServiceJwtServerUrl;
		this.airportServiceJwtClientId = airportServiceJwtClientId;
		this.airportServiceJwtClientSecret = airportServiceJwtClientSecret;
	}

	public void loadIdentifiersFromExternalApi() throws LoadingFromExternalApiException  {
		LOGGER.info("Getting JWT access token from Keycloak [{}]", airportServiceJwtServerUrl);
//		var accessToken = getJwtAccessToken("https://localhost:8083/realms/flight-log/protocol/openid-connect/token", "fls-client", "32rCPgB5F1KYTIRUddcFiJTbqADWX5HJ");
		var accessToken = getJwtAccessToken(airportServiceJwtServerUrl, airportServiceJwtClientId, airportServiceJwtClientSecret);
		
		LOGGER.info("Received JWT access token [{}]", accessToken);
		LOGGER.info("Loading airport identifiers from external api [{}]", airportServiceUrl);

		var jsonResponse = getUrlContent(airportServiceUrl, accessToken);
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
			for (JsonNode identifier : identifiers) {
				identifierSet.add(identifier.asText());
			}
			LOGGER.info("Loaded [{}] airport identifiers", identifierSet.size());
		}
	}

	public void refreshIdentifiersFromExternalApi() throws LoadingFromExternalApiException  {
		LOGGER.info("Clearing identifierSet");
		identifierSet.clear();
		
		loadIdentifiersFromExternalApi();
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
		return getUrlContent(urlString, null);
	}
	private String getUrlContent(String urlString, String jwt) throws LoadingFromExternalApiException {
		if (urlLoggingEnabled) LOGGER.info("Hitting url: [{}]", urlString);
		
		var client = HttpClient.newHttpClient();
		var httpRequestBuilder = HttpRequest.newBuilder()
		    .uri(URI.create(urlString))
		    .GET();
		    
		if (StringUtils.isNotEmpty(jwt)) {
			httpRequestBuilder.header("Authorization", "Bearer " + jwt);
		}
		
		var httpRequest = httpRequestBuilder.build();
		
		HttpResponse<String> response = null;
		try {
			response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			var message = String.format("Fetching contents of URL [%s]failed. ", urlString);
			throw new LoadingFromExternalApiException(message, e);
		}
		var httpStatusCode = response.statusCode();
		if (httpStatusCode != HttpURLConnection.HTTP_OK) {
			var message = String.format("Fetching contents of URL [%s], returned Http status code [%d]. ",
					urlString, httpStatusCode);
			if (response != null && StringUtils.isNotEmpty(response.body())) {
				message += String.format("Site message: [%s]", response.body());
			}
			throw new LoadingFromExternalApiException(message);
		}
		return response.body();

	}

	private String getJwtAccessToken(String urlString, String clientId, String clientSecret) throws LoadingFromExternalApiException {
		
		if (urlLoggingEnabled) LOGGER.info("Hitting url: [{}]", urlString);
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
		    .uri(URI.create(urlString))
		    .header("Content-Type", "application/x-www-form-urlencoded")
		    .POST(BodyPublishers.ofString("client_id="+clientId+"&client_secret="+clientSecret+"&grant_type=client_credentials"))
		    .build();

		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			var message = String.format("Fetching contents of URL [%s]failed. ", urlString);
			throw new LoadingFromExternalApiException(message, e);
		}
		var httpStatusCode = response.statusCode();
		if (httpStatusCode != HttpURLConnection.HTTP_OK) {
			var message = String.format("Fetching contents of URL [%s], returned Http status code [%d]. ",
					urlString, httpStatusCode);
			if (response != null && StringUtils.isNotEmpty(response.body())) {
				message += String.format("Site message: [%s]", response.body());
			}
			throw new LoadingFromExternalApiException(message);
		}
		var jsonResponse = response.body();
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
		return root.path("access_token").asText();
	}

}
