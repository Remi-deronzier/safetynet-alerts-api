package deronzier.remi.safetynetalerts.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import deronzier.remi.safetynetalerts.model.FireStation;
import deronzier.remi.safetynetalerts.service.FireStationService;

@RestController
public class FireStationController {

	@Autowired
	private FireStationService fireStationService;

	/**
	 * Read - Get all fire stations
	 * 
	 * @return - A list object of FireStation full filled
	 * @throws IOException
	 * @throws DatabindException
	 * @throws StreamReadException
	 */
	@GetMapping("/firestations")
	public List<FireStation> getFirestations() throws StreamReadException, DatabindException, IOException {
		return fireStationService.getFireStations();
	}

}