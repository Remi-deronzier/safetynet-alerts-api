package deronzier.remi.safetynetalerts.controller.firestation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import deronzier.remi.safetynetalerts.controller.FireStationController;
import deronzier.remi.safetynetalerts.model.firestation.FireStation;
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

	static final private List<FireStation> fireStations = new ArrayList<>();
	static final private List<Object> floodPeople = new ArrayList<>();
	static final private FireStation validFireStationForPostMethod = new FireStation();
	static final private FireStation fireStationWithNullStationField = new FireStation();
	static final private FireStation validFireStationForPutMethod = new FireStation();

	@BeforeAll
	public static void setUp() {
		fireStations.add(new FireStation());
		floodPeople.add(new HashMap<>());

		// Valid input
		validFireStationForPostMethod.setAddress("address test bis");
		validFireStationForPostMethod.setStation(7);

		// Station number is null
		fireStationWithNullStationField.setAddress("address test bis");

		// Station address is null
		validFireStationForPutMethod.setStation(7);

	}

	@Test
	public void testFindAll_whenNoFireStations_thenReturn404() throws Exception {

		mockMvc.perform(get("/firestations"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindAll() throws Exception {

		when(fireStationService.findAll())
				.thenReturn(fireStations);

		mockMvc.perform(get("/firestations"))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetFloodPersons() throws Exception {

		when(fireStationService.getFloodPersons(new int[] { 1, 2 }))
				.thenReturn(floodPeople);

		mockMvc.perform(get("/flood/stations").param("stations", "1,2"))
				.andExpect(status().isOk());
	}

	@Test
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/firestations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(validFireStationForPostMethod)))
				.andExpect(status().isCreated());
	}

	@Test
	public void tesCreate_whenNullValue_thenReturn400() throws Exception {

		mockMvc.perform(
				post("/firestations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(fireStationWithNullStationField)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testDeleteByStationNumber() throws Exception {

		mockMvc.perform(delete("/firestations/delete-by-station-number")
				.param("stationNumber", "1"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteByAddress() throws Exception {

		mockMvc.perform(delete("/firestations/delete-by-address")
				.param("address", "address test"))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/firestations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(validFireStationForPutMethod))
				.param("address", "address test"))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdate_whenNotNullAddress_thenReturn400() throws Exception {

		mockMvc.perform(put("/firestations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(validFireStationForPostMethod))
				.param("address", "address test"))
				.andExpect(status().isBadRequest());

	}

}
