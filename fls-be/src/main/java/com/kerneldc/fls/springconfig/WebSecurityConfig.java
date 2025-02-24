package com.kerneldc.fls.springconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {
	
	@Value("${application.security.disableSecurity:false}")
	private boolean disableSecurity;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
			/* CorsConfigurationSource corsConfigurationSource, */ KeycloakJwtRolesConverter keycloakJwtRolesConverter)
			throws Exception {

		DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter =
				// Using the delegating converter multiple converters can be combined
				new DelegatingJwtGrantedAuthoritiesConverter(
						// First add the default converter
						new JwtGrantedAuthoritiesConverter(),
						// Second add our custom Keycloak specific converter
						keycloakJwtRolesConverter);

		// Set up http security to use the JWT converter defined above
//		httpSecurity.oauth2ResourceServer().jwt()
//				.jwtAuthenticationConverter(jwt -> new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt), keycloakJwtRolesConverter.getUsername(jwt)));
		httpSecurity.oauth2ResourceServer((oauth2) -> oauth2
				.jwt((jwt) -> jwt.jwtAuthenticationConverter(jwtConv -> new JwtAuthenticationToken(jwtConv,
						authoritiesConverter.convert(jwtConv), keycloakJwtRolesConverter.getUsername(jwtConv)))));

		//return httpSecurity.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll())
				// .authorizeRequests(authorizeRequests ->
				// authorizeRequests.anyRequest().authenticated())
				// .authorizeRequests(authorizeRequests ->
				// authorizeRequests.anyRequest().hasRole("kerneldc-realm-user-role"))
				// .authorizeRequests(authorizeRequests ->
				// authorizeRequests.anyRequest().hasRole("SCOPE_PROFILE"))

//		httpSecurity.authorizeRequests()
//		.mvcMatchers("/sandboxController/noBearerTokenPing", "/actuator/*").permitAll()
//		.mvcMatchers("/protected/sandboxController/getUserInfo").hasRole("realm_sso-app-user-role")
//		//.mvcMatchers("/noBearerTokenPing").hasRole("realm_sso2-app-admin-role")
//		;
		if (disableSecurity) {
			LOGGER.warn("*** appliction security is currently disabled ***");
			LOGGER.warn("*** to enable set application.security.disableSecurity to false ***");
			//httpSecurity.authorizeHttpRequests ().anyRequest().permitAll();
			httpSecurity.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.anyRequest().permitAll());
		} else {
			httpSecurity.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
			.requestMatchers("/appInfoController/*", "/pingController/*", "/actuator/*").permitAll()
			.anyRequest().authenticated());
		}
		
		httpSecurity.exceptionHandling(
						exception -> exception.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
								.accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

		httpSecurity.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				;
				
		httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		return httpSecurity.build();
	}
	
}
