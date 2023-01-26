package com.fhir.client.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;

@RestController
@RequestMapping("/Patient")
public class PatientController {

    @Autowired
    private IGenericClient fhirClient;

    @PostMapping
    public MethodOutcome createPatient(@RequestBody Patient patient) {
        MethodOutcome outcome = fhirClient.create().resource(patient).execute();
        return outcome;
    }

    @GetMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Patient readPatient(@PathVariable("id") String id) {
        Patient patient = fhirClient.read().resource(Patient.class).withId(id).execute();
        return patient;
    }
    
    @GetMapping( consumes= MediaType.ALL_VALUE)
    public List<Patient> getPatientList() {
        IQuery<IBaseBundle> query = fhirClient.search().forResource(Patient.class);
        Bundle response = query.returnBundle(Bundle.class).encodedJson().execute();
        return response.getEntry().stream().map(e -> (Patient) e.getResource()).collect(Collectors.toList());
    }
    

    @PutMapping("/{id}")
    public MethodOutcome updatePatient(@PathVariable("id") String id, @RequestBody Patient patient) {
        MethodOutcome outcome = fhirClient.update().resource(patient).withId(id).execute();
        return outcome;
    }

}


