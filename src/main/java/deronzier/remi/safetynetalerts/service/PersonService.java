package deronzier.remi.safetynetalerts.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public class PersonService {
	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private Helpers helpers;

	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	public List<Person> getPersons() throws StreamReadException, DatabindException, IOException {
		return resourceRepository.getPersons();
	}

	public Map<String, Object> getPersonsCoveredFireStation(final String stationId) {
		Map<String, Integer> counters = new HashMap<>();
		counters.put("childrenCounter", 0);
		counters.put("adultCounter", 0);
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		List<FireStation> filteredFireStations = fireStations
				.stream()
				.filter(firestation -> firestation.getStation().equals(stationId))
				.collect(Collectors.toList());
		List<String> addressesToSearch = filteredFireStations
				.stream()
				.map(fireStation -> fireStation.getAddress())
				.collect(Collectors.toList());
		List<Map<String, String>> personsCovered = persons
				.stream()
				.filter(person -> addressesToSearch.contains(person.getAddress()))
				.map(person -> {
					return populatePersonsCoveredFireStation(counters, medicalRecords, person);
				})
				.collect(Collectors.toList());
		Map<String, Object> res = new HashMap<>();
		res.put("data", personsCovered);
		res.put("childrenCounter", counters.get("childrenCounter"));
		res.put("adultCounter", counters.get("adultCounter"));
		return res;
	}

	public List<String> getPersonsPhoneNumberCoveredFireStation(final String stationId) {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<FireStation> filteredFireStations = fireStations
				.stream()
				.filter(firestation -> firestation.getStation().equals(stationId))
				.collect(Collectors.toList());
		List<String> addressesToSearch = filteredFireStations
				.stream()
				.map(fireStation -> fireStation.getAddress())
				.collect(Collectors.toList());
		List<String> personsCovered = persons
				.stream()
				.filter(person -> addressesToSearch.contains(person.getAddress()))
				.map(person -> person.getPhone())
				.collect(Collectors.toList());
		return personsCovered;
	}

	private Map<String, String> populatePersonsCoveredFireStation(Map<String, Integer> counters,
			List<MedicalRecord> medicalRecords,
			Person person) {
		MedicalRecord medicalRecord = helpers.getMedicalRecord(person, medicalRecords);
		boolean isChildren = helpers.isChildren(medicalRecord);
		if (isChildren) {
			counters.put("childrenCounter", counters.get("childrenCounter") + 1);
		} else {
			counters.put("adultCounter", counters.get("adultCounter") + 1);
		}
		Map<String, String> res = new HashMap<>();
		res.put("firstName", person.getFirstName());
		res.put("lastName", person.getLastName());
		res.put("address", person.getAddress());
		res.put("phone", person.getPhone());
		return res;
	}

	private Map<String, Object> populatePersonsForSpecificAddress(Person person, List<MedicalRecord> medicalRecords) {
		Map<String, Object> res = new HashMap<>();
		res.put("firstName", person.getFirstName());
		res.put("lastName", person.getLastName());
		MedicalRecord medicalRecord = helpers.getMedicalRecord(person, medicalRecords);
		res.put("age", String.valueOf(helpers.getAge(medicalRecord.getBirthdate())));
		res.put("medications", medicalRecord.getMedications());
		res.put("allergies", medicalRecord.getAllergies());
		return res;
	}

	public Map<String, Object> getPersonsSpecificAddress(final String address) {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		List<Map<String, Object>> personsCovered = persons
				.stream()
				.filter(person -> person.getAddress().equals(address))
				.map(person -> populatePersonsForSpecificAddress(person, medicalRecords))
				.collect(Collectors.toList());
		Optional<FireStation> fireStationNumberOptional = fireStations
				.stream()
				.filter(fireStation -> fireStation.getAddress().equals(address))
				.findFirst();
		String fireStationNumber;
		if (fireStationNumberOptional.isEmpty()) {
			return helpers.buildErrorMessage();
		} else {
			fireStationNumber = fireStationNumberOptional.get().getStation();
		}
		Map<String, Object> res = new HashMap<>();
		res.put("data", personsCovered);
		res.put("station", fireStationNumber);
		return res;
	}

	public List<String> getPersonsEmailsCity(String city) {
		List<Person> persons = resourceRepository.getPersons();
		List<String> res = persons
				.stream()
				.filter(person -> person.getCity().equals(city))
				.map(person -> person.getEmail())
				.collect(Collectors.toList());
		return res;
	}

	private Map<String, Object> populatePersonInfo(Person person, List<MedicalRecord> medicalRecords) {
		Map<String, Object> res = new HashMap<>();
		res.put("lastName", person.getLastName());
		res.put("address", person.getAddress());
		MedicalRecord medicalRecord = helpers.getMedicalRecord(person, medicalRecords);
		res.put("age", String.valueOf(helpers.getAge(medicalRecord.getBirthdate())));
		res.put("email", person.getEmail());
		res.put("medications", medicalRecord.getMedications());
		return res;
	}

	public List<Object> getPersonInfo(String firstName, String lastName) {
		List<Person> persons = resourceRepository.getPersons();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		List<Object> res = persons
				.stream()
				.filter(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName))
				.map(person -> populatePersonInfo(person, medicalRecords))
				.collect(Collectors.toList());
		return res;

	}

}
