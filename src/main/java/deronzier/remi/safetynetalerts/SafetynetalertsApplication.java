package deronzier.remi.safetynetalerts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import deronzier.remi.safetynetalerts.service.FireStationService;

@SpringBootApplication
public class SafetynetalertsApplication implements CommandLineRunner {

//	@Autowired
//	private AllResourceService allResourceService;
	@Autowired
	private FireStationService fireStationService;

	public static void main(String[] args) {
		SpringApplication.run(SafetynetalertsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fireStationService.getFireStations();
	}

}
