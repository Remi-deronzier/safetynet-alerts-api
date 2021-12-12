package deronzier.remi.safetynetalerts.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import deronzier.remi.safetynetalerts.model.FireStation;
import deronzier.remi.safetynetalerts.repository.FireStationRepository;
import lombok.Data;

@Data
@Service
public class FireStationService {

	@Autowired
	private FireStationRepository firestationRepository;

	public List<FireStation> getFireStations() throws StreamReadException, DatabindException, IOException {
		return firestationRepository.getFireStations();
	}

}
