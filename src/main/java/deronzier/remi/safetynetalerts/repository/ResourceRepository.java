package deronzier.remi.safetynetalerts.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import deronzier.remi.safetynetalerts.model.FireStation;
import deronzier.remi.safetynetalerts.model.MedicalRecord;
import deronzier.remi.safetynetalerts.model.Person;

@Repository
public class ResourceRepository {

	private List<FireStation> fireStations = new ArrayList<>();
	private List<Person> persons = new ArrayList<>();
	private List<MedicalRecord> medicalRecords = new ArrayList<>();

	public List<FireStation> getFireStations() {
		return new ArrayList<FireStation>(fireStations);
	}

	public List<Person> getPersons() {
		return new ArrayList<Person>(persons);
	}

	public List<MedicalRecord> getMedicalRecords() {
		return new ArrayList<MedicalRecord>(medicalRecords);
	}

	public void getResources()
			throws StreamReadException, DatabindException, IOException {
		File jsonDataFile = new File("src/main/resources/data.json");
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode rootNode = objectMapper.readTree(jsonDataFile);

		JsonNode personNode = rootNode.path("persons");
		JsonNode fireStationNode = rootNode.path("firestations");
		JsonNode medicalRecordNode = rootNode.path("medicalrecords");

		Iterator<JsonNode> personElements = personNode.elements();
		Iterator<JsonNode> fireStationElements = fireStationNode.elements();
		Iterator<JsonNode> medicalRecordElements = medicalRecordNode.elements();

		while (personElements.hasNext()) {
			JsonNode jsonNodeResource = personElements.next();
			Person resource = objectMapper.treeToValue(jsonNodeResource, Person.class);
			persons.add(resource);
		}
		while (fireStationElements.hasNext()) {
			JsonNode jsonNodeResource = fireStationElements.next();
			FireStation resource = objectMapper.treeToValue(jsonNodeResource, FireStation.class);
			fireStations.add(resource);
		}
		while (medicalRecordElements.hasNext()) {
			JsonNode jsonNodeResource = medicalRecordElements.next();
			MedicalRecord resource = objectMapper.treeToValue(jsonNodeResource, MedicalRecord.class);
			medicalRecords.add(resource);
		}
	}

}
