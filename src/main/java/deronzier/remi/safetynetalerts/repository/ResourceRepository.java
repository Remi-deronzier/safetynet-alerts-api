package deronzier.remi.safetynetalerts.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import deronzier.remi.safetynetalerts.model.firestation.FireStation;
import deronzier.remi.safetynetalerts.model.medicalrecord.MedicalRecord;
import deronzier.remi.safetynetalerts.model.person.Person;

@Repository
public class ResourceRepository {

	private List<FireStation> fireStations = new ArrayList<>();
	private List<Person> persons = new ArrayList<>();
	private List<MedicalRecord> medicalRecords = new ArrayList<>();
	ObjectMapper objectMapper = new ObjectMapper();
	String path = "src/main/resources/data.json";

	public List<FireStation> getFireStations() {
		return new ArrayList<FireStation>(fireStations);
	}

	public List<Person> getPersons() {
		return new ArrayList<Person>(persons);
	}

	public List<MedicalRecord> getMedicalRecords() {
		return new ArrayList<MedicalRecord>(medicalRecords);
	}

	public void getResources() throws IOException {
		File jsonDataFile = new File(path);

		JsonNode rootNode;
		try {
			rootNode = objectMapper.readTree(jsonDataFile);
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
		} catch (IOException e) {
			throw new IOException("Une erreur de lecture du fichier s'est produite");
		}

	}

	public void writeResources(List<Person> persons, List<FireStation> fireStations,
			List<MedicalRecord> medicalRecords) throws IOException {
		ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
		Map<String, Object> data = new HashMap<>();
		data.put("firestations", fireStations);
		data.put("persons", persons);
		data.put("medicalrecords", medicalRecords);

		try {
			writer.writeValue(Paths.get(path).toFile(), data);
		} catch (IOException e) {
			throw new IOException("Une erreur s'est produite lors de l'écriture dans le fichier");
		}

	}

}
