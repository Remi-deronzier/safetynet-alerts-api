package deronzier.remi.safetynetalerts.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import deronzier.remi.safetynetalerts.model.FireStation;
import deronzier.remi.safetynetalerts.model.MedicalRecord;
import deronzier.remi.safetynetalerts.model.Person;
import deronzier.remi.safetynetalerts.repository.ResourceRepository;

@Service
public class FireStationService {

	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private Helpers helpers;

	public List<FireStation> getFireStations() throws StreamReadException, DatabindException, IOException {
		return resourceRepository.getFireStations();
	}

	public List<Object> getFloodPersons(String[] stationIds) {
		List<Object> res = new ArrayList<>();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		for (String stationId : stationIds) {
			List<FireStation> filteredFireStations = fireStations
					.stream()
					.filter(firestation -> firestation.getStation().equals(stationId))
					.collect(Collectors.toList());
			List<String> addressesToSearch = filteredFireStations
					.stream()
					.map(fireStation -> fireStation.getAddress())
					.collect(Collectors.toList());
			for (String address : addressesToSearch) {
				res.add(getFloodPersonsAddress(address));
			}
		}
		return res;
	}

	private Map<String, Object> getFloodPersonsAddress(String address) {
		List<Person> persons = resourceRepository.getPersons();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		List<Map<String, Object>> personsCovered = persons
				.stream()
				.filter(person -> person.getAddress().equals(address))
				.map(person -> {
					return populatePersonsCoveredFireStation(medicalRecords, person);
				})
				.collect(Collectors.toList());
		Map<String, Object> res = new HashMap<>();
		res.put(address, personsCovered);
		return res;
	}

	private Map<String, Object> populatePersonsCoveredFireStation(
			List<MedicalRecord> medicalRecords,
			Person person) {
		MedicalRecord medicalRecord = helpers.getMedicalRecord(person, medicalRecords);
		long age = helpers.getAge(medicalRecord.getBirthdate());
		Map<String, Object> res = new HashMap<>();
		res.put("lastName", person.getLastName());
		res.put("phone", person.getPhone());
		res.put("age", String.valueOf(age));
		res.put("medications", medicalRecord.getMedications());
		res.put("allergies", medicalRecord.getAllergies());
		return res;
	}
}
