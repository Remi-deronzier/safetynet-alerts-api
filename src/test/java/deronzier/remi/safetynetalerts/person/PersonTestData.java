package deronzier.remi.safetynetalerts.person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deronzier.remi.safetynetalerts.model.person.Person;

public class PersonTestData {

	public static final Person VALID_PERSON_POST_METHOD = new Person();
	static final Person VALID_PERSON_PUT_METHOD = new Person();
	static final Person EMPTY_PERSON = new Person();

	static final Map<String, Object> INFO_TAG_TO_FILLED_PERSONAL_INFO = new HashMap<>();
	static final Map<String, Object> INFO_TAG_TO_EMPTY_PERSONAL_INFO = new HashMap<>();
	static final List<String> RANDOM_TEST_STRING_LIST = Arrays.asList(new String[] { "test1", "test2" });
	static final List<Object> RANDOM_TEST_OBJECT_LIST = Arrays.asList(new Object[] { "test1", "test2" });
	static final List<Person> ALL_PERSONS = new ArrayList<>();

	public static void setUp() {
		// Valid person for post method
		VALID_PERSON_POST_METHOD.setAddress("address");
		VALID_PERSON_POST_METHOD.setFirstName("John");
		VALID_PERSON_POST_METHOD.setLastName("Doe");
		VALID_PERSON_POST_METHOD.setCity("Paris");
		VALID_PERSON_POST_METHOD.setZip("75000");
		VALID_PERSON_POST_METHOD.setPhone("0606060606");
		VALID_PERSON_POST_METHOD.setEmail("test@gmail.com");

		// Valid person for put method
		VALID_PERSON_PUT_METHOD.setAddress("address");
		VALID_PERSON_PUT_METHOD.setCity("Paris");
		VALID_PERSON_PUT_METHOD.setZip("75000");
		VALID_PERSON_PUT_METHOD.setPhone("0606060606");
		VALID_PERSON_PUT_METHOD.setEmail("test@gmail.com");

		// Servlet mock responses
		INFO_TAG_TO_FILLED_PERSONAL_INFO.put("data", Arrays.asList(new String[] { "test1", "test2" }));
		INFO_TAG_TO_EMPTY_PERSONAL_INFO.put("data", Arrays.asList(new String[] {}));
		ALL_PERSONS.add(VALID_PERSON_POST_METHOD);
	}

}
