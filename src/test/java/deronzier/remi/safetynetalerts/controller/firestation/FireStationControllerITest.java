package deronzier.remi.safetynetalerts.controller.firestation;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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

import deronzier.remi.safetynetalerts.model.firestation.FireStation;

@SpringBootTest(properties = { "sp.init.filepath.data=src/main/resources/static/test/data-test.json" })
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FireStationControllerITest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	// Initialize a valid fire station
	static FireStation validFireStation = new FireStation();

	// Define 2 test files
	static final private File source = new File("src/main/resources/static/test/data-not-modified.json");
	static final private File dest = new File("src/main/resources/static/test/data-test.json");

	@BeforeAll
	public static void setUp() throws IOException {
		// Valid input
		validFireStation.setAddress("address test");
		validFireStation.setStation(7);

		// Reset data
		resetDataFile();
	}

	@SuppressWarnings("resource")
	static void resetDataFile() throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destChannel = null;

		try {
			sourceChannel = new FileInputStream(source).getChannel();
			destChannel = new FileOutputStream(dest).getChannel();
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			sourceChannel.close();
			destChannel.close();
		}

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
						.content(mapper.writeValueAsString(validFireStation)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.address", is("address test")));
	}

	@Test
	@Order(4)
	public void testDeleteByStationNumber() throws Exception {

		mockMvc.perform(delete("/firestations/delete-by-station-number")
				.param("stationNumber", "1"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(5)
	public void testDeleteByAddress() throws Exception {

		mockMvc.perform(delete("/firestations/delete-by-address")
				.param("address", "1509 Culver St"))
				.andExpect(status().isOk());
	}

}
