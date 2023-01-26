package com.fhir.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Configuration
public class FhirConfig {
	
	@Bean
	public IGenericClient fhirClient() {
		FhirContext fhirContext = FhirContext.forR4();
		IGenericClient client = fhirContext.newRestfulGenericClient("https://hapi.fhir.org/baseR4");
		return client;
	}
}
