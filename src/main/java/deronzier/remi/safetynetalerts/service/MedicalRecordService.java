package deronzier.remi.safetynetalerts.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import deronzier.remi.safetynetalerts.model.MedicalRecord;
import deronzier.remi.safetynetalerts.model.Person;
import deronzier.remi.safetynetalerts.repository.ResourceRepository;

@Service
public class MedicalRecordService {

	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private Helpers helpers;

	public List<MedicalRecord> getMedicalRecords() throws StreamReadException, DatabindException, IOException {
		return resourceRepository.getMedicalRecords();
	}

	private Map<String, String> populatePersonsForSpecificAddress(MedicalRecord medicalRecord) {
		Map<String, String> res = new HashMap<>();
		res.put("firstName", medicalRecord.getFirstName());
		res.put("lastName", medicalRecord.getLastName());
		res.put("age", String.valueOf(helpers.getAge(medicalRecord.getBirthdate())));
		return res;
	}

	public Map<String, Object> getChildrenSpecificAddress(final String address) {
		List<Person> persons = resourceRepository.getPersons();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		Optional<Person> optionalPerson = persons
				.stream()
				.filter(person -> person.getAddress().equalsIgnoreCase(address))
				.findFirst();
		String lastNameSought;
		if (optionalPerson.isEmpty()) {
			return helpers.buildErrorMessage();
		} else {
			lastNameSought = optionalPerson.get().getLastName();
		}
		List<MedicalRecord> family = medicalRecords
				.stream()
				.filter(medicalRecord -> medicalRecord.getLastName().equals(lastNameSought))
				.collect(Collectors.toList());
		List<Map<String, String>> children = family
				.stream()
				.filter(medicalRecord -> helpers.isChildren(medicalRecord))
				.map(medicalRecord -> populatePersonsForSpecificAddress(medicalRecord))
				.collect(Collectors.toList());
		List<Map<String, String>> adults = family
				.stream()
				.filter(medicalRecord -> !helpers.isChildren(medicalRecord))
				.map(medicalRecord -> populatePersonsForSpecificAddress(medicalRecord))
				.collect(Collectors.toList());
		Map<String, Object> res = new HashMap<>();
		res.put("children", children);
		res.put("adults", adults);
		return res;
	}

}
