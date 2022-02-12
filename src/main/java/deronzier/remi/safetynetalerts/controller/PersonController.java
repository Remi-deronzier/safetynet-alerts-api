package deronzier.remi.safetynetalerts.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import deronzier.remi.safetynetalerts.model.Person;
import deronzier.remi.safetynetalerts.service.PersonService;

@RestController
@Controller
public class PersonController {

	@Autowired
	private PersonService personService;

	/**
	 * @return A list object of all the people
	 * @throws StreamReadException
	 * @throws DatabindException
	 * @throws IOException
	 */
	@GetMapping("/persons")
	public List<Person> getPersons() throws StreamReadException, DatabindException, IOException {
		return personService.getPersons();
	}

	/**
	 * @param StationNumber the station number of the corresponding fire station
	 * @return A list object of Person full filled
	 * @throws IOException
	 * @throws DatabindException
	 * @throws StreamReadException
	 */
	@GetMapping("/firestation")
	public Map<String, Object> getPersonsCoveredFireStation(@RequestParam final String stationNumber) {
		return personService.getPersonsCoveredFireStation(stationNumber);

	}

	/**
	 * @param firestation
	 * @return List of all people's phone number that are close to a specific
	 *         fireStation
	 */
	@GetMapping("/phoneAlert")
	public List<String> getPersonsPhoneNumberCoveredFireStation(@RequestParam final String firestation) {
		return personService.getPersonsPhoneNumberCoveredFireStation(firestation);
	}

	/**
	 * @param address
	 * @return List of all people near specific address
	 */
	@GetMapping("/fire")
	public Map<String, Object> getPersonsSpecificAddress(@RequestParam final String address) {
		return personService.getPersonsSpecificAddress(address);
	}

	/**
	 * @param city
	 * @return List of people's email in the same city
	 */
	@GetMapping("/communityEmail")
	public List<String> getPersonsEmailsCity(@RequestParam final String city) {
		return personService.getPersonsEmailsCity(city);
	}

	/**
	 * @param firstName
	 * @param lastName
	 * @return Info about a specific person
	 */
	@GetMapping("/personInfo")
	public List<Object> getPersonInfo(@RequestParam final String firstName, final String lastName) {
		return personService.getPersonInfo(firstName, lastName);
	}

}
