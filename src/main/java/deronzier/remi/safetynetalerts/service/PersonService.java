package deronzier.remi.safetynetalerts.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import deronzier.remi.safetynetalerts.model.Person;
import deronzier.remi.safetynetalerts.repository.PersonRepository;
import lombok.Data;

@Data
@Service
public class PersonService {

	@Autowired
	private PersonRepository personRepository;

	public List<Person> getPersons() throws StreamReadException, DatabindException, IOException {
		return personRepository.getPersons();
	}

}
