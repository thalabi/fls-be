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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerneldc.fls.exeption.ApplicationException;
import com.kerneldc.fls.util.namedparameter.LongParam;
import com.kerneldc.fls.util.namedparameter.NamedParameter;
import com.kerneldc.fls.util.namedparameter.NamedParameterSet;
import com.kerneldc.fls.util.namedparameter.SetOfStringParam;
import com.kerneldc.fls.util.namedparameter.StringParam;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HttpService {

	private final boolean urlLoggingEnabled;
	private final String flightLogOauth2ServerUrl;
	private final String airportServiceApiUrl;
	private final String flightLogPendingServiceApiUrl;

	private final ObjectMapper objectMapper;

	public enum RequestTypeEnum {
		FLIGHT_LOG_JWT_TOKEN,
		AIRPORT_IDENTIFIERS,
		FLIGHT_LOG_PENDING_ADD;
	}
	
	public HttpService(@Value("${httpservice.url.logging.enabled:false}") boolean urlLoggingEnabled,
			@Value("${flightlog.oauth2.server.url}") String flightLogOauth2ServerUrl,
			@Value("${airport.service.url}") String airportServiceApiUrl,
			@Value("${flightlog.pending.service.url}") String flightLogPendingServiceApiUrl,
			ObjectMapper objectMapper) {
		this.urlLoggingEnabled = urlLoggingEnabled;
		this.flightLogOauth2ServerUrl = flightLogOauth2ServerUrl;
		this.airportServiceApiUrl = airportServiceApiUrl;
		this.flightLogPendingServiceApiUrl = flightLogPendingServiceApiUrl;
		this.objectMapper = objectMapper;
	}

	public NamedParameterSet processRequest(RequestTypeEnum requestTypeEnum, Set<NamedParameter> data) throws ApplicationException {
		return processRequest(requestTypeEnum, data, StringUtils.EMPTY);
	}
	public NamedParameterSet processRequest(RequestTypeEnum requestTypeEnum, String jwt) throws ApplicationException {
		return processRequest(requestTypeEnum, Set.of(), jwt);
	}
	
	public NamedParameterSet processRequest(RequestTypeEnum requestTypeEnum, Set<NamedParameter> data, String jwt) throws ApplicationException {
		var parameterSet = new NamedParameterSet(data);
		switch (requestTypeEnum) {
			case FLIGHT_LOG_JWT_TOKEN -> {
				return fetchJwtAccessToken(parameterSet);
			}
			case AIRPORT_IDENTIFIERS -> {
				return loadIdentifiersFromExternalApi(jwt);
			}
			case FLIGHT_LOG_PENDING_ADD -> {
				addFlightLogPending(parameterSet, jwt);
				return null;
			}
		}
		return null;
	}
	
	private void addFlightLogPending(NamedParameterSet parameters, String jwt) throws ApplicationException {
		
		if (urlLoggingEnabled) LOGGER.info("Hitting url: [{}]", flightLogPendingServiceApiUrl);
		
		var flightLogPendingJson = toFlightLogPendingJson(parameters);
		HttpClient client = HttpClient.newHttpClient();
		var httpRequestBuilder = HttpRequest.newBuilder()
		    .uri(URI.create(flightLogPendingServiceApiUrl))
		    .header("Content-Type", MediaType.APPLICATION_JSON.toString())
		    .POST(BodyPublishers.ofString(flightLogPendingJson));
		
		httpRequestBuilder.header("Authorization", "Bearer " + jwt);

		var httpRequest = httpRequestBuilder.build();

		HttpResponse<String> response = null;
		try {
			response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			var message = String.format("Sending request to URL [%s] failed. ", flightLogPendingServiceApiUrl);
			throw new ApplicationException(message, e);
		}

		var httpStatusCode = response.statusCode();
		if (httpStatusCode != HttpURLConnection.HTTP_CREATED) {
			var message = String.format("Sending request to URL [%s], returned Http status code [%d]. ",
					flightLogPendingServiceApiUrl, httpStatusCode);
			if (response != null && StringUtils.isNotEmpty(response.body())) {
				message += String.format("Site message: [%s]", response.body());
			}
			throw new ApplicationException(message);
		}

	}
	
	private String toFlightLogPendingJson(NamedParameterSet parameterSet) throws ApplicationException {
		var flightLogPendingJsonNode = objectMapper.createObjectNode();
		flightLogPendingJsonNode.put("flightDate", parameterSet.get("flightDate", String.class));
		flightLogPendingJsonNode.put("routeFrom", parameterSet.get("routeFrom", String.class));
		flightLogPendingJsonNode.put("routeTo", parameterSet.get("routeTo", String.class));
		flightLogPendingJsonNode.put("flightTime", parameterSet.get("flightTime", Float.class));
		flightLogPendingJsonNode.put("registration", parameterSet.get("registration", String.class));
		flightLogPendingJsonNode.put("makeModel", parameterSet.get("makeModel", String.class));
		try {
			return objectMapper.writeValueAsString(flightLogPendingJsonNode);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new ApplicationException("Error creating json string", e);
		}
	}

	private NamedParameterSet loadIdentifiersFromExternalApi(String jwt) throws ApplicationException  {
		
		if (urlLoggingEnabled) LOGGER.info("Hitting url: [{}]", airportServiceApiUrl);

		var client = HttpClient.newHttpClient();
		var httpRequest = HttpRequest.newBuilder()
		    .uri(URI.create(airportServiceApiUrl))
		    .GET()
			.header("Authorization", "Bearer " + jwt)
			.build();

		HttpResponse<String> response = null;
		try {
			response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			var message = String.format("Fetching contents of URL [%s] failed. ", airportServiceApiUrl);
			throw new ApplicationException(message, e);
		}
		var httpStatusCode = response.statusCode();
		if (httpStatusCode != HttpURLConnection.HTTP_OK) {
			var message = String.format("Fetching contents of URL [%s], returned Http status code [%d]. ",
					airportServiceApiUrl, httpStatusCode);
			if (response != null && StringUtils.isNotEmpty(response.body())) {
				message += String.format("Site message: [%s]", response.body());
			}
			throw new ApplicationException(message);
		}

		var jsonResponse = response.body();
		var mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(jsonResponse);
		} catch (JsonProcessingException e) {
			var message = String.format("Exception when parsing json response: %s", jsonResponse);
			LOGGER.error(message, e);
			throw new ApplicationException(message);
		}
		LOGGER.debug(root.toPrettyString());
		var identifiers = root.path("identifiers");
		Set<String> identifierSet= new HashSet<>();
		if (identifiers.isArray()) {
			for (JsonNode identifier : identifiers) {
				identifierSet.add(identifier.asText());
			}
		}
		
		return new NamedParameterSet(Set.of(new SetOfStringParam("identifierSet", identifierSet)));
	}

	private NamedParameterSet fetchJwtAccessToken(NamedParameterSet parameters) throws ApplicationException {
		
		if (urlLoggingEnabled) LOGGER.info("Hitting url: [{}]", flightLogOauth2ServerUrl);
		
		var oauth2ClientId = parameters.get("oauth2clientId", String.class);
		var oauth2ClientSecret = parameters.get("oauth2ClientSecret", String.class);
		
		var client = HttpClient.newHttpClient();
		var request = HttpRequest.newBuilder()
		    .uri(URI.create(flightLogOauth2ServerUrl))
		    .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString())
			.POST(BodyPublishers.ofString("client_id=" + oauth2ClientId + "&client_secret=" + oauth2ClientSecret
					+ "&grant_type=client_credentials"))
		    .build();

		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			var message = String.format("Fetching contents of URL [%s] failed. ", flightLogOauth2ServerUrl);
			throw new ApplicationException(message, e);
		}
		var httpStatusCode = response.statusCode();
		if (httpStatusCode != HttpURLConnection.HTTP_OK) {
			var message = String.format("Fetching contents of URL [%s], returned Http status code [%d]. ",
					flightLogOauth2ServerUrl, httpStatusCode);
			if (response != null && StringUtils.isNotEmpty(response.body())) {
				message += String.format("Site message: [%s]", response.body());
			}
			throw new ApplicationException(message);
		}
		var jsonResponse = response.body();
		var mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(jsonResponse);
		} catch (JsonProcessingException e) {
			var message = String.format("Exception when parsing json response: %s", jsonResponse);
			LOGGER.error(message, e);
			throw new ApplicationException(message);
		}
		LOGGER.debug(root.toPrettyString());
		
		var jwt = root.path("access_token").asText();
		var expiresIn = root.path("expires_in").asLong();
		return new NamedParameterSet(Set.of(new StringParam("jwt", jwt), new LongParam("expiresIn", expiresIn)));
	}

}
