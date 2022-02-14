package deronzier.remi.safetynetalerts.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import deronzier.remi.safetynetalerts.exception.firestation.FireStationAlreadyExistsException;
import deronzier.remi.safetynetalerts.exception.firestation.FireStationNotFoundException;
import deronzier.remi.safetynetalerts.model.firestation.FireStation;
import deronzier.remi.safetynetalerts.model.medicalrecord.MedicalRecord;
import deronzier.remi.safetynetalerts.model.person.Person;
import deronzier.remi.safetynetalerts.repository.ResourceRepository;
import deronzier.remi.safetynetalerts.utils.Helpers;

@Service
public class FireStationService {

	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private Helpers helpers;

	private boolean findFireStation(String address, FireStation firestation) {
		return firestation.getAddress().equalsIgnoreCase(address);
	}

	private boolean findFireStation(int stationNumber, FireStation firestation) {
		return firestation.getStation() == stationNumber;
	}

	private FireStation findFireStation(String address, List<FireStation> fireStations) {
		return fireStations
				.stream()
				.filter(fireStation -> findFireStation(address, fireStation))
				.findFirst()
				.orElse(null);
	}

	private List<FireStation> findFireStation(int stationNumber, List<FireStation> fireStations) {
		return fireStations
				.stream()
				.filter(fireStation -> findFireStation(stationNumber, fireStation))
				.collect(Collectors.toList());
	}

	private int indexOfFireStation(String address, List<FireStation> fireStations) {
		return fireStations.indexOf(findFireStation(address, fireStations));
	}

	public List<Object> getFloodPersons(int[] stationIds) {
		List<Object> res = new ArrayList<>();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		for (int stationId : stationIds) {
			List<FireStation> filteredFireStations = fireStations
					.stream()
					.filter(firestation -> firestation.getStation() == stationId)
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

	// CRUD

	public FireStation save(FireStation newFireStation) throws IOException, FireStationAlreadyExistsException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		String address = newFireStation.getAddress();
		FireStation potentialFireStation = findFireStation(address, fireStations);
		if (potentialFireStation != null) {
			throw new FireStationAlreadyExistsException("Fire station already exists in DB");
		}
		fireStations.add(newFireStation);
		resourceRepository.writeResources(persons, fireStations, medicalRecords);
		return newFireStation;
	}

	public List<FireStation> findAll() {
		return resourceRepository.getFireStations();
	}

	public FireStation update(FireStation fireStation, String address) throws IOException, FireStationNotFoundException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		int index = indexOfFireStation(address, fireStations);
		if (index == -1) {
			throw new FireStationNotFoundException("Fire station not found");
		} else {
			FireStation fireStationToUpdate = findFireStation(address, fireStations);
			fireStation.setAddress(fireStationToUpdate.getAddress());
			fireStations.set(index, fireStation);
			resourceRepository.writeResources(persons, fireStations, medicalRecords);
			return fireStation;
		}
	}

	public String deleteByAddress(String address) throws IOException, FireStationNotFoundException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		FireStation fireStationToDelete = findFireStation(address, fireStations);
		if (fireStationToDelete == null) {
			throw new FireStationNotFoundException("Fire station not found");
		}
		fireStations.remove(fireStationToDelete);
		resourceRepository.writeResources(persons, fireStations, medicalRecords);
		return "Fire station" + " " + fireStationToDelete.getAddress() + " successfully deleted";
	}

	public String deleteByNumber(int stationNumber) throws IOException, FireStationNotFoundException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		List<FireStation> fireStationsToDelete = findFireStation(stationNumber, fireStations);
		if (fireStationsToDelete.isEmpty()) {
			throw new FireStationNotFoundException("No fire station found");
		}
		for (FireStation fireStation : fireStationsToDelete) {
			fireStations.remove(fireStation);
		}
		resourceRepository.writeResources(persons, fireStations, medicalRecords);
		return "Fire stations with n°" + stationNumber + " successfully deleted";
	}

}
