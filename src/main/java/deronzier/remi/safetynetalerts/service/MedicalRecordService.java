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
import deronzier.remi.safetynetalerts.exception.medicalrecord.MedicalRecordAlreadyExistsException;
import deronzier.remi.safetynetalerts.exception.medicalrecord.MedicalRecordNotFoundException;
import deronzier.remi.safetynetalerts.exception.person.PersonNotFoundException;
import deronzier.remi.safetynetalerts.model.firestation.FireStation;
import deronzier.remi.safetynetalerts.model.medicalrecord.MedicalRecord;
import deronzier.remi.safetynetalerts.model.person.Person;
import deronzier.remi.safetynetalerts.repository.ResourceRepository;
import deronzier.remi.safetynetalerts.utils.FindObject;
import deronzier.remi.safetynetalerts.utils.Helpers;

@Service
public class MedicalRecordService {

	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private Helpers helpers;
	@Autowired
	private FindObject<MedicalRecord> findObjectInMedicalRecords;
	@Autowired
	private FindObject<Person> findObjectInPersons;

	private Map<String, Object> populatePersonsForSpecificAddress(MedicalRecord medicalRecord) {
		Map<String, Object> res = new HashMap<>();
		res.put("firstName", medicalRecord.getFirstName());
		res.put("lastName", medicalRecord.getLastName());
		res.put("age", helpers.getAge(medicalRecord.getBirthdate()));
		return res;
	}

	public Map<String, Object> getChildrenSpecificAddress(final String address) throws AddressNotFound {
		List<Person> persons = resourceRepository.getPersons();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		Optional<Person> optionalPerson = persons
				.stream()
				.filter(person -> person.getAddress().equalsIgnoreCase(address))
				.findFirst();
		String lastNameSought;
		if (optionalPerson.isEmpty()) {
			throw new AddressNotFound("No such address");
		} else {
			lastNameSought = optionalPerson.get().getLastName();
		}
		List<MedicalRecord> family = medicalRecords
				.stream()
				.filter(medicalRecord -> medicalRecord.getLastName().equals(lastNameSought))
				.collect(Collectors.toList());
		List<Map<String, Object>> children = family
				.stream()
				.filter(medicalRecord -> helpers.isChildren(medicalRecord))
				.map(medicalRecord -> populatePersonsForSpecificAddress(medicalRecord))
				.collect(Collectors.toList());
		List<Map<String, Object>> adults = family
				.stream()
				.filter(medicalRecord -> !helpers.isChildren(medicalRecord))
				.map(medicalRecord -> populatePersonsForSpecificAddress(medicalRecord))
				.collect(Collectors.toList());
		Map<String, Object> res = new HashMap<>();
		res.put("children", children);
		res.put("adults", adults);
		return res;
	}

	// CRUD

	public MedicalRecord save(MedicalRecord newMedicalRecord)
			throws IOException, MedicalRecordAlreadyExistsException, PersonNotFoundException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		String firstName = newMedicalRecord.getFirstName();
		String lastName = newMedicalRecord.getLastName();
		Person potentialPerson = findObjectInPersons.findPerson(firstName, lastName, persons);
		if (potentialPerson == null) {
			throw new PersonNotFoundException(
					"Person not found, you cannot add a medical record for non existant person");
		}
		MedicalRecord potentialMedicalRecord = findObjectInMedicalRecords.findPerson(firstName, lastName,
				medicalRecords);
		if (potentialMedicalRecord != null) {
			throw new MedicalRecordAlreadyExistsException("Medical record already exists in DB");
		}
		medicalRecords.add(newMedicalRecord);
		resourceRepository.writeResources(persons, fireStations, medicalRecords);
		return newMedicalRecord;
	}

	public List<MedicalRecord> findAll() {
		return resourceRepository.getMedicalRecords();
	}

	public MedicalRecord update(MedicalRecord medicalRecord, String firstName, String lastName)
			throws IOException, MedicalRecordNotFoundException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		int index = findObjectInMedicalRecords.indexOfPerson(firstName, lastName, medicalRecords);
		if (index == -1) {
			throw new MedicalRecordNotFoundException("Medical record not found");
		} else {
			MedicalRecord medicalRecordToUpdate = findObjectInMedicalRecords.findPerson(firstName, lastName,
					medicalRecords);
			medicalRecord.setFirstName(medicalRecordToUpdate.getFirstName());
			medicalRecord.setLastName(medicalRecordToUpdate.getLastName());
			medicalRecords.set(index, medicalRecord);
			resourceRepository.writeResources(persons, fireStations, medicalRecords);
			return medicalRecord;
		}
	}

	public String delete(String firstName, String lastName) throws IOException, MedicalRecordNotFoundException {
		List<Person> persons = resourceRepository.getPersons();
		List<FireStation> fireStations = resourceRepository.getFireStations();
		List<MedicalRecord> medicalRecords = resourceRepository.getMedicalRecords();
		MedicalRecord medicalRecordToDelete = findObjectInMedicalRecords.findPerson(firstName, lastName,
				medicalRecords);
		if (medicalRecordToDelete == null) {
			throw new MedicalRecordNotFoundException("Medical record not found");
		}
		medicalRecords.remove(medicalRecordToDelete);
		resourceRepository.writeResources(persons, fireStations, medicalRecords);
		return "Medical record of " + firstName + " " + lastName + " successfully deleted";
	}

}
