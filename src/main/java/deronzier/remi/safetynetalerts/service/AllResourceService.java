package deronzier.remi.safetynetalerts.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import deronzier.remi.safetynetalerts.model.FireStation;
import deronzier.remi.safetynetalerts.model.MedicalRecord;
import deronzier.remi.safetynetalerts.model.Person;
import deronzier.remi.safetynetalerts.repository.FireStationRepository;
import deronzier.remi.safetynetalerts.repository.MedicalRecordRepository;
import deronzier.remi.safetynetalerts.repository.PersonRepository;

@Service
public class AllResourceService {

	@Autowired
	private FireStationRepository firestationRepository;
	@Autowired
	private MedicalRecordRepository medicalRecordRepository;
	@Autowired
	private PersonRepository personRepository;

	public List<Person> persons;
	public List<FireStation> fireStations;
	public List<MedicalRecord> medicalRecords;

	public void getFireStations() throws StreamReadException, DatabindException, IOException {
		fireStations = firestationRepository.getFireStations("firestations");
	}

	public void getMedicalRecords()
			throws StreamReadException, DatabindException, IOException {
		medicalRecords = medicalRecordRepository.getMedicalRecords();
	}

	public void getPersons() throws StreamReadException, DatabindException, IOException {
		persons = personRepository.getPersons();
	}

//	public List<Person> getPersonsCoveredFireStation(final int stationId) {
//		List<FireStation> firestationsFiltered = fireStations.stream()
//				.filter(firestation -> firestation.getStation() == stationId).toList();
//		List<String> addresses = firestationsFiltered.stream().map(fireStation -> fireStation.getAddress()).toList();
//		List<Person> personsCoverd = persons.stream().filter(person -> addresses.contains(person.getAddress()))
//				.toList();
//		return personsCoverd;
//	}

}
