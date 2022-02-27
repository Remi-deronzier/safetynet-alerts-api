package deronzier.remi.safetynetalerts.controller.firestation;

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
public class FireStationControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@BeforeAll
	public static void setUp() throws IOException {
		// Prepare data for tests
		FireStationTestData.setUp();

		// Reset data in test file
		FileTestManagement.resetDataFile();
	}

	@Test
	@Order(1)
	public void testGetfindAll() throws Exception {
		mockMvc.perform(get("/firestations"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].station", is(3)))
				.andExpect(jsonPath("$[0].address", is("1509 Culver St")));
	}

	@Test
	@Order(2)
	public void testGetFloodPersons() throws Exception {

		mockMvc.perform(get("/flood/stations").param("stations", "1,2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].['644 Gershwin Cir'][0].lastName", is("Duncan")));
	}

	@Test
	@Order(3)
	public void testCreate() throws Exception {

		mockMvc.perform(
				post("/firestations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_POST_METHOD)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.address", is("address test")));
	}

	@Test
	@Order(4)
	public void tesCreate_whenNullValue_thenReturn400() throws Exception {

		mockMvc.perform(
				post("/firestations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(FireStationTestData.FIRE_STATION_EMPTY_STATION_NUMBER)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(5)
	public void testUpdate() throws Exception {

		mockMvc.perform(put("/firestations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_PUT_METHOD))
				.param("address", "834 Binoc Ave"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(6)
	public void testUpdate_whenNotNullAddress_thenReturn400() throws Exception {

		mockMvc.perform(put("/firestations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(FireStationTestData.VALID_FIRE_STATION_POST_METHOD))
				.param("address", "834 Binoc Ave"))
				.andExpect(status().isBadRequest());

	}

	@Test
	@Order(7)
	public void testDeleteByStationNumber() throws Exception {

		mockMvc.perform(delete("/firestations/delete-by-station-number")
				.param("stationNumber", "1"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(8)
	public void testDeleteByAddress() throws Exception {

		mockMvc.perform(delete("/firestations/delete-by-address")
				.param("address", "1509 Culver St"))
				.andExpect(status().isOk());
	}

}
