package deronzier.remi.safetynetalerts.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import deronzier.remi.safetynetalerts.model.MedicalRecord;
import deronzier.remi.safetynetalerts.model.Person;

@Component
public class Helpers {

	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	public long getAge(String birthDay) {
		Date now = new Date(System.currentTimeMillis());
		Date birthday = null;
		try {
			birthday = sdf.parse(birthDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long differenceInTime = now.getTime() - birthday.getTime();
		long differenceInYears = TimeUnit.MILLISECONDS
				.toDays(differenceInTime)
				/ 365l;
		return differenceInYears;
	}

	public boolean isChildren(MedicalRecord medicalRecord) {
		boolean isChildren = getAge(medicalRecord.getBirthdate()) <= 18;
		return isChildren;
	}

	public Map<String, Object> buildErrorMessage() {
		Map<String, Object> error = new HashMap<>();
		error.put("message", "Aucune adresse ne correspond à celle spécifiée");
		return error;
	}

	public MedicalRecord getMedicalRecord(Person person, List<MedicalRecord> medicalRecords) {
		MedicalRecord medicalRecord = medicalRecords
				.stream()
				.filter(localMedicalRecord -> localMedicalRecord.getFirstName()
						.equals(person.getFirstName()) &&
						localMedicalRecord.getLastName().equals(person.getLastName()))
				.findFirst()
				.orElse(null);
		return medicalRecord;
	}

}
