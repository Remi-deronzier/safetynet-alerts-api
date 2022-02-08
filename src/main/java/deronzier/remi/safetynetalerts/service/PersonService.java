//package deronzier.remi.safetynetalerts.service;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.fasterxml.jackson.core.exc.StreamReadException;
//import com.fasterxml.jackson.databind.DatabindException;
//
//import deronzier.remi.safetynetalerts.model.FireStation;
//import deronzier.remi.safetynetalerts.model.Person;
//import deronzier.remi.safetynetalerts.repository.PersonRepository;
//import lombok.Data;
//
//@Data
//@Service
//public class PersonService {
//
//	@Autowired
//	private PersonRepository personRepository;
//	@Autowired
//	private AllResourceService allResourceService;
//
//	public List<Person> getPersons() throws StreamReadException, DatabindException, IOException {
//		return personRepository.getPersons();
//	}
//
//	public List<Person> getPersonsCoveredFireStation(final int stationId) {
//		System.out.println(allResourceService.fireStations);
//		System.out.println(allResourceService.fireStations.get(0) instanceof FireStation);
//		List<FireStation> firestationsFiltered = allResourceService.fireStations.stream()
//				.filter(firestation -> firestation.getStation() == stationId).collect(Collectors.toList());
//		System.out.println(firestationsFiltered);
//		List<String> addresses = firestationsFiltered.stream().map(fireStation -> fireStation.getAddress())
//				.collect(Collectors.toList());
//		List<Person> personsCoverd = allResourceService.persons.stream()
//				.filter(person -> addresses.contains(person.getAddress()))
//				.collect(Collectors.toList());
//		return personsCoverd;
//	}
//}
