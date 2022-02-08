package deronzier.remi.safetynetalerts.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class FireStationRepository<T> {
	File jsonDataFile = new File("src/main/resources/data.json");
	ObjectMapper objectMapper = new ObjectMapper();
	private final Class<T> genericType;
	// private final String resourceName;

//	public FireStationRepository(Class<T> typeParameterClass) {
//		this.typeParameterClass = typeParameterClass;
//		this.resourceName = typeParameterClass.getClass().getSimpleName().toLowerCase() + "s";
//	}

	@SuppressWarnings("unchecked")
	public FireStationRepository() {
		this.genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(),
				FireStationRepository.class);
		// this.resourceName =
		// this.typeParameterClass.getClass().getSimpleName().toLowerCase() + "s";
	}

//	public List<FireStation> getFireStations() throws StreamReadException, DatabindException, IOException {
//		List<FireStation> fireStations = new ArrayList<>();
//		JsonNode rootNode = objectMapper.readTree(jsonDataFile);
//		JsonNode fireStationsNode = rootNode.path("firestations");
//		Iterator<JsonNode> elements = fireStationsNode.elements();
//		while (elements.hasNext()) {
//			JsonNode jsonNodeFireStation = elements.next();
//			FireStation fireStation = objectMapper.treeToValue(jsonNodeFireStation, FireStation.class);
//			fireStations.add(fireStation);
//		}
//		return fireStations;
//	}

	public List<T> getFireStations(String resourceName) throws StreamReadException, DatabindException, IOException {
		List<T> fireStations = new ArrayList<>();
		JsonNode rootNode = objectMapper.readTree(jsonDataFile);
		JsonNode fireStationsNode = rootNode.path(resourceName);
		Iterator<JsonNode> elements = fireStationsNode.elements();
		while (elements.hasNext()) {
			JsonNode jsonNodeFireStation = elements.next();
			T fireStation = objectMapper.treeToValue(jsonNodeFireStation, this.genericType);
			fireStations.add(fireStation);
		}
		return fireStations;
	}

}
