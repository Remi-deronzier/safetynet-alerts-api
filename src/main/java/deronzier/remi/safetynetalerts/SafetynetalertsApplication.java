package deronzier.remi.safetynetalerts;

import java.io.File;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class SafetynetalertsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SafetynetalertsApplication.class, args);
	}

	@Override
	public void run(String[] args) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		File jsonDataFile = new File("src/main/resources/data.json");
		Map<String, Object> map = objectMapper.readValue(jsonDataFile, new TypeReference<Map<String, Object>>() {
		});
		System.out.println(map);
		System.out.println(map.get("persons"));

	}

}
