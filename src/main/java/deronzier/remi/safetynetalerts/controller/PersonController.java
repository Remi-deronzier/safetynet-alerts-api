package deronzier.remi.safetynetalerts.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import deronzier.remi.safetynetalerts.exception.AddressNotFound;
import deronzier.remi.safetynetalerts.exception.person.PersonAlreadyExistsException;
import deronzier.remi.safetynetalerts.exception.person.PersonNotFoundException;
import deronzier.remi.safetynetalerts.model.CreateClass;
import deronzier.remi.safetynetalerts.model.UpdateClass;
import deronzier.remi.safetynetalerts.model.person.Person;
import deronzier.remi.safetynetalerts.service.PersonService;

@RestController
public class PersonController {

	@Autowired
	private PersonService personService;

	/**
	 * @param stationNumber
	 * @return List of people next to stationNumber, the number of children and the
	 *         number of adult
	 */
	@GetMapping("/firestation")
	public Map<String, Object> getPersonsCoveredFireStation(
			@RequestParam final int stationNumber) {

		if (stationNumber <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Station number must be positive");
		}

		@SuppressWarnings("unchecked")
		List<Object> personsCovered = (List<Object>) personService.getPersonsCoveredFireStation(stationNumber)
				.get("data");
		if (personsCovered.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found");
		} else {
			return personService.getPersonsCoveredFireStation(stationNumber);
		}
	}

	/**
	 * @param firestation
	 * @return List of all people's phone number near firestation
	 */
	@GetMapping("/phoneAlert")
	public List<String> getPersonsPhoneNumberCoveredFireStation(@RequestParam final int firestation) {
		if (firestation <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Station number must be positive");
		}
		if (personService.getPersonsPhoneNumberCoveredFireStation(firestation).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No phone found");
		}
		return personService.getPersonsPhoneNumberCoveredFireStation(firestation);
	}

	/**
	 * @param address
	 * @return List of people covered by fire station at specific address and the
	 *         fire station number
	 */
	@GetMapping("/fire")
	public Map<String, Object> getPersonsSpecificAddress(@RequestParam final String address) {
		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> personsCovered = (List<Map<String, Object>>) personService
					.getPersonsSpecificAddress(address).get("data");
			if (personsCovered.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found");
			}
			return personService.getPersonsSpecificAddress(address);
		} catch (AddressNotFound anf) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, anf.getMessage());
		}
	}

	/**
	 * @param city
	 * @return List of people's email in the same city
	 */
	@GetMapping("/communityEmail")
	public List<String> getPersonsEmailsCity(@RequestParam final String city) {
		if (personService.getPersonsEmailsCity(city).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No email found");
		}
		return personService.getPersonsEmailsCity(city);
	}

	/**
	 * @param firstName
	 * @param lastName
	 * @return Info about a specific person
	 */
	@GetMapping("/personInfo")
	public List<Object> getPersonInfo(@RequestParam final String firstName, final String lastName) {
		if (personService.getPersonInfo(firstName, lastName).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"No person found for such first name and last name");
		}
		return personService.getPersonInfo(firstName, lastName);
	}

	// CRUD

	/**
	 * @return List of all persons
	 */
	@GetMapping("/persons")
	public List<Person> findAll() {
		if (personService.findAll().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found");
		}
		return personService.findAll();
	}

	/**
	 * @param newPerson
	 * @param result
	 * @return New person created
	 */
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping("/persons")
	Person create(@Validated(CreateClass.class) @RequestBody Person newPerson, BindingResult result) {
		if (result.hasErrors()) {
			List<String> errorMessages = new ArrayList<>();
			result.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
			String errorMessage = String.join(", ", errorMessages);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
		}
		try {
			return personService.save(newPerson);
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		} catch (PersonAlreadyExistsException paee) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, paee.getMessage());
		}
	}

	/**
	 * @param firstName
	 * @param lastName
	 * @return A message to warn whether the person has been deleted successfully
	 */
	@DeleteMapping("/persons")
	String delete(@RequestParam final String firstName, final String lastName) {
		try {
			return personService.delete(firstName, lastName);
		} catch (PersonNotFoundException pnf) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, pnf.getMessage());
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		}
	}

	/**
	 * @param person
	 * @param firstName
	 * @param lastName
	 * @return Updated person
	 */
	@PutMapping("/persons")
	Person update(@RequestParam final String firstName, final String lastName,
			@Validated(UpdateClass.class) @RequestBody Person person, BindingResult result) {
		if (person.getFirstName() != null || person.getLastName() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name and last name cannot be modified");
		}
		if (result.hasErrors()) {
			List<String> errorMessages = new ArrayList<>();
			result.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
			String errorMessage = String.join(", ", errorMessages);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
		}
		try {
			return personService.update(person, firstName, lastName);
		} catch (PersonNotFoundException pnf) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, pnf.getMessage());
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		}
	}

}
