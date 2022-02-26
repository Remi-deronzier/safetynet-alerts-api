package deronzier.remi.safetynetalerts.medicalrecord;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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

import deronzier.remi.safetynetalerts.model.medicalrecord.MedicalRecord;
import deronzier.remi.safetynetalerts.model.person.Person;
import deronzier.remi.safetynetalerts.utils.FileTestManagement;

@SpringBootTest(properties = { "sp.init.filepath.data=src/main/resources/static/test/data-test.json" })
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MedicalRecordControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	static final private MedicalRecord validMedicalRecordForPostMethod = new MedicalRecord();
	static final private MedicalRecord validMedicalRecordForPutMethod = new MedicalRecord();
	static final private MedicalRecord invalidMedicalRecord = new MedicalRecord();
	static final private MedicalRecord invalidMedicalRecordNoMedications = new MedicalRecord();

	private static final Person personValidForPostMethod = new Person();

	@BeforeAll
	public static void setUp() throws IOException {
		// Valid person
		personValidForPostMethod.setAddress("address");
		personValidForPostMethod.setFirstName("Robert");
		personValidForPostMethod.setLastName("Doe");
		personValidForPostMethod.setCity("Paris");
		personValidForPostMethod.setZip("75000");
		personValidForPostMethod.setPhone("0606060606");
		personValidForPostMethod.setEmail("test@gmail.com");

		// Valid medical record for post method
		List<String> medications = new ArrayList<>();
		medications.add("medication1");
		medications.add("medication2");

		List<String> allergies = new ArrayList<>();
		allergies.add("allergy1");
		allergies.add("allergy2");

		validMedicalRecordForPostMethod.setFirstName("Robert");
		validMedicalRecordForPostMethod.setLastName("Doe");
		validMedicalRecordForPostMethod.setBirthdate(new GregorianCalendar(2000, Calendar.FEBRUARY, 12).getTime());
		validMedicalRecordForPostMethod.setMedications(medications);
		validMedicalRecordForPostMethod.setAllergies(allergies);

		// Valid medical record for put method
		validMedicalRecordForPutMethod.setBirthdate(new GregorianCalendar(2001, Calendar.FEBRUARY, 11).getTime());
		validMedicalRecordForPutMethod.setMedications(medications);
		validMedicalRecordForPutMethod.setAllergies(allergies);

		// Invalid medical record for: no medications
		invalidMedicalRecordNoMedications.setBirthdate(new GregorianCalendar(2001, Calendar.FEBRUARY, 11).getTime());
		invalidMedicalRecordNoMedications.setAllergies(allergies);

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
						.content(mapper.writeValueAsString(personValidForPostMethod)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.['firstName']", is("Robert")))
				.andExpect(jsonPath("$.['address']", is("address")));
	}

	@Test
	@Order(5)
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(validMedicalRecordForPostMethod)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName", is("Robert")))
				.andExpect(jsonPath("$.birthdate", is("02/11/2000")));
	}

	@Test
	@Order(6)
	public void tesCreate_whenNullValue_thenReturns400() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(invalidMedicalRecord)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(7)
	public void tesCreate_whenMedicalRecordAlreadyCreated_thenReturns409() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(validMedicalRecordForPostMethod)))
				.andExpect(status().isConflict());
	}

	@Test
	@Order(8)
	public void testUpdate_whenNotNullFirstName_thenReturns400() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(validMedicalRecordForPostMethod))
				.param("firstName", "Robert")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(9)
	public void testUpdate_whenNullMedications_thenReturns400() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(invalidMedicalRecordNoMedications))
				.param("firstName", "Robert")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(10)
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(validMedicalRecordForPutMethod))
				.param("firstName", "Robert")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());

	}

	@Test
	@Order(11)
	public void testDelete() throws Exception {

		mockMvc.perform(delete("/medical-records")
				.param("firstName", "Robert")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}

}
