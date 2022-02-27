package deronzier.remi.safetynetalerts.medicalrecord;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import deronzier.remi.safetynetalerts.controller.MedicalRecordController;
import deronzier.remi.safetynetalerts.exception.AddressNotFound;
import deronzier.remi.safetynetalerts.exception.medicalrecord.MedicalRecordAlreadyExistsException;
import deronzier.remi.safetynetalerts.exception.medicalrecord.MedicalRecordNotFoundException;
import deronzier.remi.safetynetalerts.exception.person.PersonNotFoundException;
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

	@BeforeAll
	public static void setUp() {
		// Prepare data for tests
		MedicalRecordTestData.setUp();

	}

	@Test
	public void testFindAll_whenNoMedicalRecord_thenReturn404() throws Exception {

		mockMvc.perform(get("/medical-records"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindAll() throws Exception {

		when(medicalRecordService.findAll())
				.thenReturn(MedicalRecordTestData.ALL_MEDICAL_RECORDS);

		mockMvc.perform(get("/medical-records"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsCoveredFireStation() throws Exception {

		when(medicalRecordService.getChildrenSpecificAddress("address test"))
				.thenReturn(MedicalRecordTestData.FAMILY_MEMBERS_TO_FILLED_INFO);

		mockMvc.perform(get("/childAlert").param("address", "address test"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetPersonsCoveredFireStation_whenNoFamilyMember_thenReturn404() throws Exception {

		when(medicalRecordService.getChildrenSpecificAddress("address test"))
				.thenReturn(MedicalRecordTestData.FAMILY_MEMBERS_TO_EMPTY_INFO);

		mockMvc.perform(get("/childAlert").param("address", "address test"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetPersonsCoveredFireStation_whenNoCorrespondingAddress_thenReturn400() throws Exception {

		when(medicalRecordService.getChildrenSpecificAddress("address test"))
				.thenThrow(new AddressNotFound());

		mockMvc.perform(get("/childAlert").param("address", "address test"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_POST_METHOD)))
				.andExpect(status().isCreated());
	}

	@Test
	public void tesCreate_whenNullValue_thenReturn400() throws Exception {

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(MedicalRecordTestData.EMPTY_MEDICAL_RECORD)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void tesCreate_whenWritingProblemInTheFile_thenReturn500() throws Exception {

		when(medicalRecordService.save(any(MedicalRecord.class)))
				.thenThrow(new IOException());

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_POST_METHOD)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void tesCreate_whenNoCorrespondingPerson_thenReturn409() throws Exception {

		when(medicalRecordService.save(any(MedicalRecord.class)))
				.thenThrow(new PersonNotFoundException());

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_POST_METHOD)))
				.andExpect(status().isConflict());
	}

	@Test
	public void tesCreate_whenNoMedicalRecord_thenReturn409() throws Exception {

		when(medicalRecordService.save(any(MedicalRecord.class)))
				.thenThrow(new MedicalRecordAlreadyExistsException("Medical record already exists in DB"));

		mockMvc.perform(
				post("/medical-records")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_POST_METHOD)))
				.andExpect(status().isConflict());
	}

	@Test
	public void testDelete() throws Exception {

		mockMvc.perform(delete("/medical-records")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDelete_whenWritingProblemInTheFile_thenReturn500() throws Exception {

		when(medicalRecordService.delete("John", "Doe"))
				.thenThrow(new IOException());

		mockMvc.perform(delete("/medical-records")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testDelete_whenNoMedicalRecord_thenReturn404() throws Exception {

		when(medicalRecordService.delete("John", "Doe"))
				.thenThrow(new MedicalRecordNotFoundException());

		mockMvc.perform(delete("/medical-records")
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testUpdate_whenNotNullFirstName_thenReturn400() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_POST_METHOD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdate_whenNullFields_thenReturn400() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(MedicalRecordTestData.EMPTY_MEDICAL_RECORD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_PUT_METHOD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isOk());

	}

	@Test
	public void testUpdate_whenWritingProblemInTheFile_thenReturn500() throws Exception {

		when(medicalRecordService.update(any(MedicalRecord.class), any(String.class), any(String.class)))
				.thenThrow(new IOException());

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_PUT_METHOD))
				.param("firstName", "James")
				.param("lastName", "Cameron"))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void testUpdate_whenNoMedicalRecord_thenReturn404() throws Exception {

		when(medicalRecordService.update(any(MedicalRecord.class), any(String.class), any(String.class)))
				.thenThrow(new MedicalRecordNotFoundException());

		mockMvc.perform(put("/medical-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(MedicalRecordTestData.VALID_MEDICAL_RECORD_PUT_METHOD))
				.param("firstName", "John")
				.param("lastName", "Doe"))
				.andExpect(status().isNotFound());

	}

}
