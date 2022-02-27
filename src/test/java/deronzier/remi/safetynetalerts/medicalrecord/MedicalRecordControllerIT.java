package deronzier.remi.safetynetalerts.medicalrecord;

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

import deronzier.remi.safetynetalerts.person.PersonTestData;
import deronzier.remi.safetynetalerts.utils.FileTestManagement;

@SpringBootTest(properties = { "sp.init.filepath.data=src/main/resources/static/test/data-test.json" })
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MedicalRecordControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@BeforeAll
	public static void setUp() throws IOException {
		// Prepare data for tests
		MedicalRecordTestData.setUp();
		PersonTestData.setUp();

		// Reset data
		FileTestManagement.resetDataFile();

	}

	@Test
	@Order(1)
	public void testFindAll() throws Exception {

		mockMvc.perform(get("/medical-records"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].firstName", is("John")))
				.andExpect(jsonPath("$[0].birthdate", is("03/06/1984")));
	}

	@Test
	@Order(2)
	public void testGetPersonsCoveredFireStation() throws Exception {

		mockMvc.perform(get("/childAlert").param("address", "1509 Culver St"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.children[0].firstName", is("Tenley")))
				.andExpect(jsonPath("$.children[0].age", is(10)));
	}

	@Test
	@Order(3)
	public void testGetPersonsCoveredFireStation_whenBadAddress_thenReturn400() throws Exception {

		mockMvc.perform(get("/childAlert").param("address", "address test"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(4)
	public void testCreatePerson() throws Exception {

		mockMvc.perform(
				post("/persons")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(PersonTestData.VALID_PERSON_POST_METHOD)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.['firstName']", is("John")))
				.andExpect(jsonPath("$.['address']", is("address")));
	}

	@Test
	@Order(5)
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_POST_METHOD)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName", is("John")))
				.andExpect(jsonPath("$.lastName", is("Doe")))
				.andExpect(jsonPath("$.birthdate", is("02/10/2014")));
	}

	@Test
	@Order(6)
	public void tesCreate_whenNullValue_thenReturn400() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(MedicalRecordTestData.EMPTY_MEDICAL_RECORD)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(7)
	public void tesCreate_whenMedicalRecordAlreadyCreated_thenReturn409() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_POST_METHOD)))
				.andExpect(status().isConflict());
	}

	@Test
	@Order(8)
	public void testUpdate_whenNotNullFirstName_thenReturns400() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_POST_METHOD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(9)
	public void testUpdate_whenNullMedication_thenReturn400() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(MedicalRecordTestData.MEDICAL_RECORD_EMPTY_MEDICATIONS))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(10)
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_PUT_METHOD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());

	}

	@Test
	@Order(11)
	public void testDelete() throws Exception {

		mockMvc.perform(delete("/medical-records")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}

}
