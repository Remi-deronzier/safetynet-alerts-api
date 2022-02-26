package deronzier.remi.safetynetalerts.medicalrecord;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

import deronzier.remi.safetynetalerts.controller.MedicalRecordController;
import deronzier.remi.safetynetalerts.model.medicalrecord.MedicalRecord;
import deronzier.remi.safetynetalerts.repository.ResourceRepository;
import deronzier.remi.safetynetalerts.service.MedicalRecordService;

@WebMvcTest(controllers = MedicalRecordController.class)
public class MedicalRecordControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ResourceRepository resourceRepository;

	@MockBean
	private MedicalRecordService medicalRecordService;

	@Autowired
	private ObjectMapper mapper;

	static final private List<MedicalRecord> medicalRecords = new ArrayList<>();
	private static final Map<String, Object> familyMembers = new HashMap<>();
	private static final Map<String, Object> noFamilyMembers = new HashMap<>();

	static final private MedicalRecord validMedicalRecordForPostMethod = new MedicalRecord();
	static final private MedicalRecord validMedicalRecordForPutMethod = new MedicalRecord();
	static final private MedicalRecord invalidMedicalRecord = new MedicalRecord();

	@BeforeAll
	public static void setUp() {
		// All medical records
		medicalRecords.add(new MedicalRecord());

		// Filled family members
		List<Map<String, Object>> children = new ArrayList<>();
		Map<String, Object> firstTestChild = new HashMap<>();
		firstTestChild.put("firstName", "John");
		Map<String, Object> secondTestChild = new HashMap<>();
		secondTestChild.put("firstName", "Anna");
		children.add(firstTestChild);
		children.add(secondTestChild);

		List<Map<String, Object>> adults = new ArrayList<>();
		Map<String, Object> firstTestAdult = new HashMap<>();
		firstTestChild.put("firstName", "James");
		Map<String, Object> secondTestAdult = new HashMap<>();
		secondTestAdult.put("firstName", "Lucie");
		children.add(firstTestAdult);
		children.add(secondTestAdult);

		familyMembers.put("children", children);
		familyMembers.put("adults", adults);

		// Empty family members
		List<Map<String, Object>> emptyChildren = new ArrayList<>();
		List<Map<String, Object>> emptyAdults = new ArrayList<>();
		noFamilyMembers.put("children", emptyChildren);
		noFamilyMembers.put("adults", emptyAdults);

		// Valid medical record for post method
		List<String> medications = new ArrayList<>();
		medications.add("medication1");
		medications.add("medication2");

		List<String> allergies = new ArrayList<>();
		allergies.add("allergy1");
		allergies.add("allergy2");

		validMedicalRecordForPostMethod.setFirstName("John");
		validMedicalRecordForPostMethod.setLastName("Doe");
		validMedicalRecordForPostMethod.setBirthdate(new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime());
		validMedicalRecordForPostMethod.setMedications(medications);
		validMedicalRecordForPostMethod.setAllergies(allergies);

		// Valid medical record for put method
		validMedicalRecordForPutMethod.setBirthdate(new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime());
		validMedicalRecordForPutMethod.setMedications(medications);
		validMedicalRecordForPutMethod.setAllergies(allergies);

	}

	@Test
	public void testFindAll_whenNoMedicalRecord_thenReturn404() throws Exception {

		mockMvc.perform(get("/medical-records"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindAll() throws Exception {

		when(medicalRecordService.findAll())
				.thenReturn(medicalRecords);

		mockMvc.perform(get("/medical-records"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsCoveredFireStation() throws Exception {

		when(medicalRecordService.getChildrenSpecificAddress("address test"))
				.thenReturn(familyMembers);

		mockMvc.perform(get("/childAlert").param("address", "address test"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsCoveredFireStation_whenNoFamilyMember_thenReturn404() throws Exception {

		when(medicalRecordService.getChildrenSpecificAddress("address test"))
				.thenReturn(noFamilyMembers);

		mockMvc.perform(get("/childAlert").param("address", "address test"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(validMedicalRecordForPostMethod)))
				.andExpect(status().isCreated());
	}

	@Test
	public void tesCreate_whenNullValue_thenReturns400() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(invalidMedicalRecord)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testDelete() throws Exception {

		mockMvc.perform(delete("/medical-records")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdate_whenNotNullFirstName_thenReturns400() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(validMedicalRecordForPostMethod))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(validMedicalRecordForPutMethod))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());

	}

}
