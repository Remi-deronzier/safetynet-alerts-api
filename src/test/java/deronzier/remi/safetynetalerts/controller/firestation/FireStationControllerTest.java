package deronzier.remi.safetynetalerts.controller.firestation;

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

import deronzier.remi.safetynetalerts.controller.FireStationController;
import deronzier.remi.safetynetalerts.exception.firestation.FireStationAlreadyExistsException;
import deronzier.remi.safetynetalerts.exception.firestation.FireStationNotFoundException;
import deronzier.remi.safetynetalerts.repository.ResourceRepository;
import deronzier.remi.safetynetalerts.service.FireStationService;

@WebMvcTest(controllers = FireStationController.class)
public class FireStationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ResourceRepository resourceRepository;

	@MockBean
	private FireStationService fireStationService;

	@Autowired
	private ObjectMapper mapper;

	@BeforeAll
	public static void setUp() {
		// Set up data for tests
		FireStationTestData.setUp();
	}

	@Test
	public void testFindAll_whenNoFireStation_thenReturn404() throws Exception {

		mockMvc.perform(get("/firestations"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindAll() throws Exception {

		when(fireStationService.findAll())
				.thenReturn(FireStationTestData.ALL_FIRE_STATIONS);

		mockMvc.perform(get("/firestations"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetFloodPersons() throws Exception {

		when(fireStationService.getFloodPersons(new int[] { 1, 2 }))
				.thenReturn(FireStationTestData.PEOPLE_IMPACTED_BY_FLOOD);

		mockMvc.perform(get("/flood/stations").param("stations", "1,2"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetFloodPersons_whenNoPerson_thenReturn404() throws Exception {

		mockMvc.perform(get("/flood/stations").param("stations", "1,2"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/firestations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_POST_METHOD)))
				.andExpect(status().isCreated());
	}

	@Test
	public void tesCreate_whenNullValue_thenReturn400() throws Exception {

		mockMvc.perform(
				post("/firestations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(FireStationTestData.FIRE_STATION_EMPTY_STATION_NUMBER)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void tesCreate_whenFireStationAlreadyExist_thenReturn409() throws Exception {

		when(fireStationService.save(FireStationTestData.VALID_FIRE_STATION_POST_METHOD))
				.thenThrow(new FireStationAlreadyExistsException("Fire station already exists in DB"));

		mockMvc.perform(
				post("/firestations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_POST_METHOD)))
				.andExpect(status().isConflict());
	}

	@Test
	public void tesCreate_whenWritingProblemInTheFile_thenReturn500() throws Exception {

		when(fireStationService.save(FireStationTestData.VALID_FIRE_STATION_POST_METHOD))
				.thenThrow(new IOException());

		mockMvc.perform(
				post("/firestations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_POST_METHOD)))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testDeleteByStationNumber() throws Exception {

		mockMvc.perform(delete("/firestations/delete-by-station-number")
				.param("stationNumber", "1"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteByStation_whenWritingProblemInTheFile_thenReturn500() throws Exception {

		when(fireStationService.deleteByNumber(1)).thenThrow(new IOException());

		mockMvc.perform(delete("/firestations/delete-by-station-number")
				.param("stationNumber", "1"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testDeleteByStationNumber_whenNoFireStation_thenReturn404() throws Exception {

		when(fireStationService.deleteByNumber(1)).thenThrow(new FireStationNotFoundException());

		mockMvc.perform(delete("/firestations/delete-by-station-number")
				.param("stationNumber", "1"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteByAddress() throws Exception {

		mockMvc.perform(delete("/firestations/delete-by-address")
				.param("address", "address test"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteByAddress_whenWritingProblemInTheFile_thenReturn500() throws Exception {

		when(fireStationService.deleteByAddress("address test")).thenThrow(new IOException());

		mockMvc.perform(delete("/firestations/delete-by-address")
				.param("address", "address test"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testDeleteByAddress_whenNoFireStation_thenReturn404() throws Exception {

		when(fireStationService.deleteByAddress("address test")).thenThrow(new FireStationNotFoundException());

		mockMvc.perform(delete("/firestations/delete-by-address")
				.param("address", "address test"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/firestations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_PUT_METHOD))
				.param("address", "address test"))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdate_whenNotNullAddress_thenReturn400() throws Exception {

		mockMvc.perform(put("/firestations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_POST_METHOD))
				.param("address", "address test"))
				.andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdate_whenNegativeFireStationNumber_thenReturn400() throws Exception {

		mockMvc.perform(put("/firestations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(FireStationTestData.FIRE_STATION_NEGATIVE_STATION_NUMBER))
				.param("address", "address test"))
				.andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdate_whenNoFireStation_thenReturn404() throws Exception {

		when(fireStationService.update(FireStationTestData.VALID_FIRE_STATION_PUT_METHOD, "address test"))
				.thenThrow(new FireStationNotFoundException());

		mockMvc.perform(put("/firestations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_PUT_METHOD))
				.param("address", "address test"))
				.andExpect(status().isNotFound());

	}

	@Test
	public void testUpdate_whenWritingProblemInTheFile_thenReturn500() throws Exception {

		when(fireStationService.update(FireStationTestData.VALID_FIRE_STATION_PUT_METHOD, "address test"))
				.thenThrow(new IOException());

		mockMvc.perform(put("/firestations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_PUT_METHOD))
				.param("address", "address test"))
				.andExpect(status().isInternalServerError());

	}

}
