package deronzier.remi.safetynetalerts.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class MedicalRecordRepository {

	File jsonDataFile = new File("src/main/resources/data.json");
	ObjectMapper objectMapper = new ObjectMapper();

	public <T> List<T> getMedicalRecords() throws StreamReadException, DatabindException, IOException {
		Map<String, List<T>> map = objectMapper.readValue(jsonDataFile, new TypeReference<Map<String, List<T>>>() {
		});
		return map.get("medicalrecords");
	}

}
