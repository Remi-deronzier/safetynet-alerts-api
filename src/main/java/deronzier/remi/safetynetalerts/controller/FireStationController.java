package deronzier.remi.safetynetalerts.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import deronzier.remi.safetynetalerts.exception.firestation.FireStationAlreadyExistsException;
import deronzier.remi.safetynetalerts.exception.firestation.FireStationNotFoundException;
import deronzier.remi.safetynetalerts.model.CreateClass;
import deronzier.remi.safetynetalerts.model.UpdateClass;
import deronzier.remi.safetynetalerts.model.firestation.FireStation;
import deronzier.remi.safetynetalerts.service.FireStationService;

@RestController
public class FireStationController {

	@Autowired
	private FireStationService fireStationService;

	/**
	 * @param stations
	 * @return List of people covered by the different fire stations
	 */
	@GetMapping("/flood/stations")
	public List<Object> getFloodPersons(@RequestParam final int[] stations) {
		if (fireStationService.getFloodPersons(stations).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found");
		}
		return fireStationService.getFloodPersons(stations);
	}

	// CRUD

	/**
	 * @return List of all fire stations
	 */
	@GetMapping("/firestations")
	public List<FireStation> findAll() {
		if (fireStationService.findAll().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found");
		}
		return fireStationService.findAll();
	}

	/**
	 * @param newFireStation
	 * @param result
	 * @return New fire station created
	 */
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping("/firestations")
	FireStation create(@Validated(CreateClass.class) @RequestBody FireStation newFireStation, BindingResult result) {
		if (result.hasErrors()) {
			List<String> errorMessages = new ArrayList<>();
			result.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
			String errorMessage = String.join(", ", errorMessages);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
		}
		try {
			return fireStationService.save(newFireStation);
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		} catch (FireStationAlreadyExistsException fsaee) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, fsaee.getMessage());
		}
	}

	/**
	 * @param address
	 * @return A message to warn whether the fire station has been deleted
	 *         successfully
	 */
	@DeleteMapping("/firestations/delete-by-address")
	String deleteByAddress(@RequestParam final String address) {
		try {
			return fireStationService.deleteByAddress(address);
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		} catch (FireStationNotFoundException fsnf) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, fsnf.getMessage());
		}
	}

	/**
	 * @param stationNumber
	 * @return A message to warn whether the fire stations have been deleted
	 *         successfully
	 */
	@DeleteMapping("/firestations/delete-by-station-number")
	String deleteByStationNumber(@RequestParam final int stationNumber) {
		try {
			return fireStationService.deleteByNumber(stationNumber);
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		} catch (FireStationNotFoundException fsnf) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, fsnf.getMessage());
		}
	}

	/**
	 * @param address
	 * @param fireStation
	 * @param result
	 * @return The updated fire station
	 */
	@PutMapping("/firestations")
	FireStation update(@RequestParam final String address,
			@Validated(UpdateClass.class) @RequestBody FireStation fireStation, BindingResult result) {
		if (fireStation.getAddress() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address cannot be modified");
		}
		if (result.hasErrors()) {
			List<String> errorMessages = new ArrayList<>();
			result.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
			String errorMessage = String.join(", ", errorMessages);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
		}
		try {
			return fireStationService.update(fireStation, address);
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		} catch (FireStationNotFoundException fsnf) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, fsnf.getMessage());
		}
	}

}
