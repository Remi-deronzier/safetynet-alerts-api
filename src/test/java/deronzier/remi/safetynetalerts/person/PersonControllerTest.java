package deronzier.remi.safetynetalerts.person;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import deronzier.remi.safetynetalerts.controller.PersonController;
import deronzier.remi.safetynetalerts.exception.AddressNotFound;
import deronzier.remi.safetynetalerts.exception.person.PersonAlreadyExistsException;
import deronzier.remi.safetynetalerts.exception.person.PersonNotFoundException;
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

	@BeforeAll
	public static void setUp() {
		// Prepare data for tests
		PersonTestData.setUp();
	}

	@Test
	public void testGetPersonsCoveredFireStation() throws Exception {
		when(personService.getPersonsCoveredFireStation(3))
				.thenReturn(PersonTestData.INFO_TAG_TO_FILLED_PERSONAL_INFO);

		mockMvc.perform(get("/firestation").param("stationNumber", "3"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsCoveredFireStation_whenNoPerson_thenReturn404() throws Exception {
		when(personService.getPersonsCoveredFireStation(3))
				.thenReturn(PersonTestData.INFO_TAG_TO_EMPTY_PERSONAL_INFO);

		mockMvc.perform(get("/firestation").param("stationNumber", "3"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetPersonsCoveredFireStation_whenStationNumberIsNegative_thenReturn400() throws Exception {
		when(personService.getPersonsCoveredFireStation(-1))
				.thenReturn(PersonTestData.INFO_TAG_TO_FILLED_PERSONAL_INFO);

		mockMvc.perform(get("/firestation").param("stationNumber", "-1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetPersonsPhoneNumberCoveredFireStation_whenFireStationNumberIsNegative_thenReturn400()
			throws Exception {

		mockMvc.perform(get("/phoneAlert").param("firestation", "-1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetPersonsPhoneNumberCoveredFireStation_whenNoPhoneNumber_thenReturn404()
			throws Exception {

		when(personService.getPersonsPhoneNumberCoveredFireStation(2))
				.thenReturn(new ArrayList<>());

		mockMvc.perform(get("/phoneAlert").param("firestation", "2"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetPersonsPhoneNumberCoveredFireStation() throws Exception {
		when(personService.getPersonsPhoneNumberCoveredFireStation(3))
				.thenReturn(PersonTestData.RANDOM_TEST_STRING_LIST);

		mockMvc.perform(get("/phoneAlert").param("firestation", "3"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsSpecificAddress() throws Exception {
		when(personService.getPersonsSpecificAddress("address test"))
				.thenReturn(PersonTestData.INFO_TAG_TO_FILLED_PERSONAL_INFO);

		mockMvc.perform(get("/fire").param("address", "address test"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsSpecificAddress_whenNoPerson_thenReturn404() throws Exception {
		when(personService.getPersonsSpecificAddress("address test"))
				.thenReturn(PersonTestData.INFO_TAG_TO_EMPTY_PERSONAL_INFO);

		mockMvc.perform(get("/fire").param("address", "address test"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetPersonsSpecificAddress_whenAddressNotFound_thenReturn404() throws Exception {
		when(personService.getPersonsSpecificAddress("address test"))
				.thenThrow(new AddressNotFound("No such address"));

		mockMvc.perform(get("/fire").param("address", "address test"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetPersonsEmailCity() throws Exception {
		when(personService.getPersonsEmailsCity("Paris"))
				.thenReturn(PersonTestData.RANDOM_TEST_STRING_LIST);

		mockMvc.perform(get("/communityEmail").param("city", "Paris"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsEmailCity_whenNoEmail_thenReturn404() throws Exception {
		when(personService.getPersonsEmailsCity("Paris"))
				.thenReturn(new ArrayList<>());

		mockMvc.perform(get("/communityEmail").param("city", "Paris"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetPersonInfo() throws Exception {
		when(personService.getPersonInfo("John", "Doe"))
				.thenReturn(PersonTestData.RANDOM_TEST_OBJECT_LIST);

		mockMvc.perform(get("/personInfo")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonInfo_whenNothing_thenReturn404() throws Exception {
		when(personService.getPersonInfo("John", "Doe"))
				.thenReturn(new ArrayList<>());

		mockMvc.perform(get("/personInfo")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindAll_whenNoPerson_thenReturn404() throws Exception {

		mockMvc.perform(get("/persons"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindAll() throws Exception {

		when(personService.findAll())
				.thenReturn(PersonTestData.ALL_PERSONS);

		mockMvc.perform(get("/persons"))
				.andExpect(status().isOk());
	}

	@Test
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/persons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_POST_METHOD)))
				.andExpect(status().isCreated());
	}

	@Test
	public void testCreate_whenPersonAlreadyExist_thenReturn409() throws Exception {
		when(personService.save(PersonTestData.VALID_PERSON_POST_METHOD))
				.thenThrow(new PersonAlreadyExistsException());

		mockMvc.perform(
				post("/persons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_POST_METHOD)))
				.andExpect(status().isConflict());
	}

	@Test
	public void testCreate_whenWritingProblemInTheFile_thenReturn500() throws Exception {
		when(personService.save(PersonTestData.VALID_PERSON_POST_METHOD))
				.thenThrow(new IOException());

		mockMvc.perform(
				post("/persons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_POST_METHOD)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void tesCreate_whenNullValue_thenReturn400() throws Exception {

		mockMvc.perform(
				post("/persons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(PersonTestData.EMPTY_PERSON)))
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
	public void testDelete_whenNoPersonFound_thenReturn404() throws Exception {

		when(personService.delete("John", "Doe"))
				.thenThrow(new PersonNotFoundException());

		mockMvc.perform(delete("/persons")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDelete_whenWritingProblemInTheFile_thenReturn500() throws Exception {

		when(personService.delete("John", "Doe"))
				.thenThrow(new IOException());

		mockMvc.perform(delete("/persons")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_PUT_METHOD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdate_whenAllFieldsNull_thenReturn400() throws Exception {

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(PersonTestData.EMPTY_PERSON))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdate_whenFirstNameNotNull_thenReturn400() throws Exception {

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_POST_METHOD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdate_whenWritingProblemInTheFile_thenReturn500() throws Exception {
		when(personService.update(PersonTestData.VALID_PERSON_PUT_METHOD, "John", "Doe"))
				.thenThrow(new IOException());

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_PUT_METHOD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void testUpdate_whenNoPerson_thenReturn404() throws Exception {

		when(personService.update(PersonTestData.VALID_PERSON_PUT_METHOD, "John", "Doe"))
				.thenThrow(new PersonNotFoundException("No such person"));

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_PUT_METHOD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isNotFound());

	}

}
