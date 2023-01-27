package com.fhir.client.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
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

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Patient readPatient(@PathVariable("id") String id) {
		Patient patient = fhirClient.read().resource(Patient.class).withId(id).execute();
		return patient;
	}

	@GetMapping(consumes = MediaType.ALL_VALUE)
	public void getPatientList() {
		IQuery<IBaseBundle> query = fhirClient.search().forResource(Patient.class).count(100);
		Bundle response = query.returnBundle(Bundle.class).encodedJson().execute();
		List<Patient> patientList = response.getEntry().stream().map(e -> (Patient) e.getResource())
				.collect(Collectors.toList());
		boolean loopbreak = false;
		try (FileWriter writer = new FileWriter("patient_details.txt")) {

			for (Patient patient : patientList) {
				if (!loopbreak) {
//			 {	System.out.println(patient.getClass().getDeclaredFields());
					Field[] fields = patient.getClass().getDeclaredFields();
					for (Field field : fields) {
						loopbreak = true;
						field.setAccessible(true);
						try {
							System.out.println(field.getName() + ": " + field.get(patient));
							writer.write(field.getName() + ": " + field.get(patient) + "\n");
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				System.out.println("____________________________");
				writer.flush();
			}
		} catch (

		IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException | IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
//		try (BufferedWriter writer = new BufferedWriter(new FileWriter("patient_list.txt"))) {
//		    for (Patient patient : patientList) {
//		        writer.write("Name: "+patient.getName().get(0).getGivenAsSingleString()+" "+patient.getName().get(0).getFamily()+"\n");
//		        if(patient.getGender()==null)
//		        	writer.write("Gender: male");
//		        else
//		        	writer.write("Gender: "+patient.getGender().toString()+"\n");
//		        if(!patient.getTelecom().isEmpty()){
//		            writer.write("Telecom: "+patient.getTelecom().get(0).getValue()+"\n");
//		        }
//		        writer.write("Address: "+patient.getAddress().get(0).getLine().get(0).getValue()+" "+patient.getAddress().get(0).getCity()+" "+patient.getAddress().get(0).getCountry()+"\n");
//		        writer.write("DOB: "+patient.getBirthDate()+"\n");
//		        writer.write("\n");
//		        writer.newLine();
//		    }
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}
	// return patientList;

	@PutMapping("/{id}")
	public MethodOutcome updatePatient(@PathVariable("id") String id, @RequestBody Patient patient) {
		MethodOutcome outcome = fhirClient.update().resource(patient).withId(id).execute();
		return outcome;
	}

}
