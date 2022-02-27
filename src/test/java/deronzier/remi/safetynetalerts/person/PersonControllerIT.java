package deronzier.remi.safetynetalerts.person;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import deronzier.remi.safetynetalerts.utils.FileTestManagement;

@SpringBootTest(properties = { "sp.init.filepath.data=src/main/resources/static/test/data-test.json" })
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@BeforeAll
	public static void setUp() throws IOException {
		// Prepare data for tests
		PersonTestData.setUp();

		// Reset data
		FileTestManagement.resetDataFile();
	}

	@Test
	@Order(1)
	public void testGetPersonsCoveredFireStation() throws Exception {

		mockMvc.perform(get("/firestation").param("stationNumber", "3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].firstName", is("John")))
				.andExpect(jsonPath("$.childrenCounter", is(3)));
	}

	@Test
	@Order(2)
	public void testGetPersonsCoveredFireStation_whenNoPerson_thenReturn404() throws Exception {

		mockMvc.perform(get("/firestation").param("stationNumber", "10"))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(3)
	public void testGetPersonsCoveredFireStation_whenStationNumberIsNegative_thenReturn400() throws Exception {

		mockMvc.perform(get("/firestation").param("stationNumber", "-1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(4)
	public void testGetPersonsPhoneNumberCoveredFireStation() throws Exception {

		mockMvc.perform(get("/phoneAlert").param("firestation", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0]", is("841-874-6513")));
	}

	@Test
	@Order(5)
	public void testGetPersonsSpecificAddress() throws Exception {

		mockMvc.perform(get("/fire").param("address", "1509 Culver St"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].['lastName']", is("Boyd")))
				.andExpect(jsonPath("$.data[0].['age']", is(38)));
	}

	@Test
	@Order(6)
	public void testGetPersonsEmailCity() throws Exception {

		mockMvc.perform(get("/communityEmail").param("city", "Culver"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0]", is("jaboyd@email.com")));
	}

	@Test
	@Order(7)
	public void testGetPersonInfo() throws Exception {

		mockMvc.perform(get("/personInfo")
				.param("firstName", "Jacob")
				.param("lastName", "Boyd"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].['medications'][0]", is("pharmacol:5000mg")))
				.andExpect(jsonPath("$[0].['age']", is(33)))
				.andExpect(jsonPath("$[0].['email']", is("drk@email.com")));
	}

	@Test
	@Order(8)
	public void testFindAll() throws Exception {

		mockMvc.perform(get("/persons"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].['firstName']", is("John")))
				.andExpect(jsonPath("$[0].['lastName']", is("Boyd")))
				.andExpect(jsonPath("$[0].['zip']", is("97451")))
				.andExpect(jsonPath("$[0].['email']", is("jaboyd@email.com")));
	}

	@Test
	@Order(9)
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/persons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_POST_METHOD)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.['firstName']", is("John")))
				.andExpect(jsonPath("$.['address']", is("address")));
	}

	@Test
	@Order(10)
	public void tesCreate_whenNullValue_thenReturn400() throws Exception {

		mockMvc.perform(
				post("/persons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(PersonTestData.EMPTY_PERSON)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(11)
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_PUT_METHOD))
				.param("firstName", "John")
				.param("lastName", "Boyd"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(12)
	public void testUpdate_whenAllFieldsNull_thenReturn400() throws Exception {

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(PersonTestData.EMPTY_PERSON))
				.param("firstName", "John")
				.param("lastName", "Boyd"))
				.andExpect(status().isBadRequest());

	}

	@Test
	@Order(13)
	public void testUpdate_whenPersonNotFound_thenReturn404() throws Exception {

		mockMvc.perform(put("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_PUT_METHOD))
				.param("firstName", "James")
				.param("lastName", "Doe"))
				.andExpect(status().isNotFound());

	}

	@Test
	@Order(14)
	public void testDelete() throws Exception {

		mockMvc.perform(delete("/persons")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}
}
