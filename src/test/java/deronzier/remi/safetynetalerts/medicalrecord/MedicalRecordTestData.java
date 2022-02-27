package deronzier.remi.safetynetalerts.medicalrecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deronzier.remi.safetynetalerts.model.medicalrecord.MedicalRecord;

public class MedicalRecordTestData {

	static final List<MedicalRecord> ALL_MEDICAL_RECORDS = new ArrayList<>();
	static final Map<String, Object> FAMILY_MEMBERS_TO_FILLED_INFO = new HashMap<>();
	static final Map<String, Object> FAMILY_MEMBERS_TO_EMPTY_INFO = new HashMap<>();

	static final MedicalRecord VALID_MEDICAL_RECORD_POST_METHOD = new MedicalRecord();
	static final MedicalRecord VALID_MEDICAL_RECORD_PUT_METHOD = new MedicalRecord();
	static final MedicalRecord EMPTY_MEDICAL_RECORD = new MedicalRecord();
	static final MedicalRecord MEDICAL_RECORD_EMPTY_MEDICATIONS = new MedicalRecord();

	public static void setUp() {
		// All medical records
		ALL_MEDICAL_RECORDS.add(new MedicalRecord());

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

		FAMILY_MEMBERS_TO_FILLED_INFO.put("children", children);
		FAMILY_MEMBERS_TO_FILLED_INFO.put("adults", adults);

		// Family with empty members
		List<Map<String, Object>> emptyChildren = new ArrayList<>();
		List<Map<String, Object>> emptyAdults = new ArrayList<>();
		FAMILY_MEMBERS_TO_EMPTY_INFO.put("children", emptyChildren);
		FAMILY_MEMBERS_TO_EMPTY_INFO.put("adults", emptyAdults);

		// Valid medical record for post method
		List<String> medications = new ArrayList<>();
		medications.add("medication1");
		medications.add("medication2");

		List<String> allergies = new ArrayList<>();
		allergies.add("allergy1");
		allergies.add("allergy2");

		VALID_MEDICAL_RECORD_POST_METHOD.setFirstName("John");
		VALID_MEDICAL_RECORD_POST_METHOD.setLastName("Doe");
		VALID_MEDICAL_RECORD_POST_METHOD.setBirthdate(new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime());
		VALID_MEDICAL_RECORD_POST_METHOD.setMedications(medications);
		VALID_MEDICAL_RECORD_POST_METHOD.setAllergies(allergies);

		// Valid medical record for put method
		VALID_MEDICAL_RECORD_PUT_METHOD.setBirthdate(new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime());
		VALID_MEDICAL_RECORD_PUT_METHOD.setMedications(medications);
		VALID_MEDICAL_RECORD_PUT_METHOD.setAllergies(allergies);

		// Invalid medical record: no medication
		MEDICAL_RECORD_EMPTY_MEDICATIONS.setBirthdate(new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime());
		MEDICAL_RECORD_EMPTY_MEDICATIONS.setAllergies(allergies);
	}

}
