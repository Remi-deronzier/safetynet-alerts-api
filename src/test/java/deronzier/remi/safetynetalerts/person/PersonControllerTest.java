package deronzier.remi.safetynetalerts.person;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import deronzier.remi.safetynetalerts.controller.PersonController;
import deronzier.remi.safetynetalerts.model.person.Person;
import deronzier.remi.safetynetalerts.repository.ResourceRepository;
import deronzier.remi.safetynetalerts.service.PersonService;

@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ResourceRepository resourceRepository;

	@MockBean
	private PersonService personService;

	@Autowired
	private ObjectMapper mapper;

	private static final Person personValidForPostMethod = new Person();
	private static final Person personValidForPutMethod = new Person();
	private static final Person personInvalid = new Person();

	private static final Map<String, Object> personsCovered = new HashMap<>();
	private static final Map<String, Object> personsCoveredWithEmptyValues = new HashMap<>();
	private static final List<String> randomTestStringList = Arrays.asList(new String[] { "test", "test2" });
	private static final List<Object> randomTestObjectList = Arrays.asList(new Object[] { "test", "test2" });
	private static final List<Person> persons = new ArrayList<>();

	@BeforeAll
	public static void setUp() {
		personValidForPostMethod.setAddress("address");
		personValidForPostMethod.setFirstName("John");
		personValidForPostMethod.setLastName("Doe");
		personValidForPostMethod.setCity("Paris");
		personValidForPostMethod.setZip("75000");
		personValidForPostMethod.setPhone("0606060606");
		personValidForPostMethod.setEmail("test@gmail.com");

		personValidForPutMethod.setAddress("address");
		personValidForPutMethod.setCity("Paris");
		personValidForPutMethod.setZip("75000");
		personValidForPutMethod.setPhone("0606060606");
		personValidForPutMethod.setEmail("test@gmail.com");

		personsCovered.put("data", Arrays.asList(new String[] { "test", "test2" }));
		personsCoveredWithEmptyValues.put("data", Arrays.asList(new String[] {}));
		persons.add(personValidForPostMethod);
	}

	@Test
	public void testGetPersonsCoveredFireStation() throws Exception {
		when(personService.getPersonsCoveredFireStation(3))
				.thenReturn(personsCovered);

		mockMvc.perform(get("/firestation").param("stationNumber", "3"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsCoveredFireStation_whenNoPersons_thenReturn404() throws Exception {
		when(personService.getPersonsCoveredFireStation(3))
				.thenReturn(personsCoveredWithEmptyValues);

		mockMvc.perform(get("/firestation").param("stationNumber", "3"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetPersonsCoveredFireStation_whenStationNumberIsNegative_thenReturn400() throws Exception {
		when(personService.getPersonsCoveredFireStation(-1))
				.thenReturn(personsCovered);

		mockMvc.perform(get("/firestation").param("stationNumber", "-1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetPersonsPhoneNumberCoveredFireStation() throws Exception {
		when(personService.getPersonsPhoneNumberCoveredFireStation(3))
				.thenReturn(randomTestStringList);

		mockMvc.perform(get("/phoneAlert").param("firestation", "3"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsSpecificAddress() throws Exception {
		when(personService.getPersonsSpecificAddress("address test"))
				.thenReturn(personsCovered);

		mockMvc.perform(get("/fire").param("address", "address test"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsEmailCity() throws Exception {
		when(personService.getPersonsEmailsCity("Paris"))
				.thenReturn(randomTestStringList);

		mockMvc.perform(get("/communityEmail").param("city", "Paris"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonInfo() throws Exception {
		when(personService.getPersonInfo("John", "Doe"))
				.thenReturn(randomTestObjectList);

		mockMvc.perform(get("/personInfo")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}

	@Test
	public void testFindAll_whenNoPersons_thenReturn404() throws Exception {

		mockMvc.perform(get("/persons"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindAll() throws Exception {

		when(personService.findAll())
				.thenReturn(persons);

		mockMvc.perform(get("/persons"))
				.andExpect(status().isOk());
	}

	@Test
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/persons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(personValidForPostMethod)))
				.andExpect(status().isCreated());
	}

	@Test
	public void tesCreate_whenNullValue_thenReturn400() throws Exception {

		mockMvc.perform(
				post("/persons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(personInvalid)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testDelete() throws Exception {

		mockMvc.perform(delete("/persons")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(personValidForPutMethod))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdate_whenNotNullFirstName_thenReturn400() throws Exception {

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(personInvalid))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());

	}

}
