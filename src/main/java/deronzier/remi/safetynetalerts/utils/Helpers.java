package deronzier.remi.safetynetalerts.utils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import deronzier.remi.safetynetalerts.model.medicalrecord.MedicalRecord;
import deronzier.remi.safetynetalerts.model.person.Person;

@Component
public class Helpers {

	public long getAge(Date birthDay) {
		Date now = new Date(System.currentTimeMillis());
		long differenceInTime = now.getTime() - birthDay.getTime();
		long differenceInYears = TimeUnit.MILLISECONDS
				.toDays(differenceInTime)
				/ 365l;
		return differenceInYears;
	}

	public boolean isChildren(MedicalRecord medicalRecord) {
		boolean isChildren = getAge(medicalRecord.getBirthdate()) <= 18;
		return isChildren;
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
