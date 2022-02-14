package deronzier.remi.safetynetalerts.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import deronzier.remi.safetynetalerts.exception.AddressNotFound;
import deronzier.remi.safetynetalerts.exception.person.PersonAlreadyExistsException;
import deronzier.remi.safetynetalerts.exception.person.PersonNotFoundException;
import deronzier.remi.safetynetalerts.model.firestation.FireStation;
import deronzier.remi.safetynetalerts.model.medicalrecord.MedicalRecord;
import deronzier.remi.safetynetalerts.model.person.Person;
import deronzier.remi.safetynetalerts.repository.ResourceRepository;
import deronzier.remi.safetynetalerts.utils.FindObject;
import deronzier.remi.safetynetalerts.utils.Helpers;

@Service
public class PersonService {
	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private Helpers helpers;
	@Autowired
	private FindObject<Person> findObject;

	public Map<String, Object> getPersonsCoveredFireStation(final int stationId) {
		Map<String, Integer> counters = new HashMap<>();
		counters.put("childrenCounter", 0);
		counters.put("adultCounter", 0);
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		List<FireStation> filteredFireStations = fireStations
				.stream()
				.filter(firestation -> firestation.getStation() == stationId)
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

	public List<String> getPersonsPhoneNumberCoveredFireStation(final int stationId) {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<FireStation> filteredFireStations = fireStations
				.stream()
				.filter(firestation -> firestation.getStation() == stationId)
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
		res.put("age", helpers.getAge(medicalRecord.getBirthdate()));
		res.put("medications", medicalRecord.getMedications());
		res.put("allergies", medicalRecord.getAllergies());
		return res;
	}

	public Map<String, Object> getPersonsSpecificAddress(final String address) throws AddressNotFound {
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
		int fireStationNumber;
		if (fireStationNumberOptional.isEmpty()) {
			throw new AddressNotFound("No such address");
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
		res.put("age", helpers.getAge(medicalRecord.getBirthdate()));
		res.put("email", person.getEmail());
		res.put("medications", medicalRecord.getMedications());
		return res;
	}

	public List<Object> getPersonInfo(String firstName, String lastName) {
		List<Person> persons = resourceRepository.getPersons();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		List<Object> res = persons
				.stream()
				.filter(person -> findObject.findPerson(firstName, lastName, person))
				.map(person -> populatePersonInfo(person, medicalRecords))
				.collect(Collectors.toList());
		return res;

	}

	// CRUD

	public Person save(Person newPerson) throws IOException, PersonAlreadyExistsException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		String firstName = newPerson.getFirstName();
		String lastName = newPerson.getLastName();
		Person potentialPerson = findObject.findPerson(firstName, lastName, persons);
		if (potentialPerson != null) {
			throw new PersonAlreadyExistsException("Person already exists in DB");
		}
		persons.add(newPerson);
		resourceRepository.writeResources(persons, fireStations, medicalRecords);
		return newPerson;
	}

	public List<Person> findAll() {
		return resourceRepository.getPersons();
	}

	public Person update(Person person, String firstName, String lastName) throws IOException, PersonNotFoundException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		int index = findObject.indexOfPerson(firstName, lastName, persons);
		if (index == -1) {
			throw new PersonNotFoundException("Person not found");
		} else {
			Person personToUpdate = findObject.findPerson(firstName, lastName, persons);
			person.setFirstName(personToUpdate.getFirstName());
			person.setLastName(personToUpdate.getLastName());
			persons.set(index, person);
			resourceRepository.writeResources(persons, fireStations, medicalRecords);
			return person;
		}
	}

	public String delete(String firstName, String lastName) throws IOException, PersonNotFoundException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		Person personToDelete = findObject.findPerson(firstName, lastName, persons);
		if (personToDelete == null) {
			throw new PersonNotFoundException("Person not found");
		}
		persons.remove(personToDelete);
		resourceRepository.writeResources(persons, fireStations, medicalRecords);
		return firstName + " " + lastName + " successfully deleted";
	}

}
