package com.kerneldc.fls.springconfig;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;

@Configuration
public class RepositoryRestConfig implements RepositoryRestConfigurer {

    @Autowired
    private EntityManager entityManager;
    
    // configure data rest
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration repositoryRestConfiguration, CorsRegistry corsRegistry) {
    	configRepositoryRest(repositoryRestConfiguration);
    }
    
    private void configRepositoryRest(RepositoryRestConfiguration repositoryRestConfiguration) {
    	// expose entity id for JPA data rest
    	repositoryRestConfiguration.exposeIdsFor(entityManager.getMetamodel().getEntities()
                .stream().map(Type::getJavaType).toArray(Class[]::new));
	}

}
