package deronzier.remi.safetynetalerts.controller.firestation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import deronzier.remi.safetynetalerts.model.firestation.FireStation;

public class FireStationTestData {

	static final List<FireStation> ALL_FIRE_STATIONS = new ArrayList<>();
	static final List<Object> PEOPLE_IMPACTED_BY_FLOOD = new ArrayList<>();
	static final FireStation VALID_FIRE_STATION_POST_METHOD = new FireStation();
	static final FireStation FIRE_STATION_EMPTY_STATION_NUMBER = new FireStation();
	static final FireStation FIRE_STATION_NEGATIVE_STATION_NUMBER = new FireStation();
	static final FireStation VALID_FIRE_STATION_PUT_METHOD = new FireStation();

	public static void setUp() {
		// Mock servlet responses
		ALL_FIRE_STATIONS.add(new FireStation());
		PEOPLE_IMPACTED_BY_FLOOD.add(new HashMap<>());

		// Valid fire station
		VALID_FIRE_STATION_POST_METHOD.setAddress("address test");
		VALID_FIRE_STATION_POST_METHOD.setStation(7);

		// Invalid fire station: station number is null
		FIRE_STATION_EMPTY_STATION_NUMBER.setAddress("address test");

		// Invalid fire station: address is null
		VALID_FIRE_STATION_PUT_METHOD.setStation(7);

		// Invalid fire station: number is negative
		FIRE_STATION_NEGATIVE_STATION_NUMBER.setStation(-2);
	}

}
