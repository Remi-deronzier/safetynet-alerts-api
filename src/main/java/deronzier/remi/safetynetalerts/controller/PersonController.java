package deronzier.remi.safetynetalerts.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import deronzier.remi.safetynetalerts.model.Person;
import deronzier.remi.safetynetalerts.service.PersonService;

@RestController
public class PersonController {

	@Autowired
	private PersonService personService;

	/**
	 * Read - Get all persons
	 * 
	 * @return - A list object of Person full filled
	 * @throws IOException
	 * @throws DatabindException
	 * @throws StreamReadException
	 */
	@GetMapping("/persons")
	public List<Person> getPersons() throws StreamReadException, DatabindException, IOException {
		return personService.getPersons();
	}

}
