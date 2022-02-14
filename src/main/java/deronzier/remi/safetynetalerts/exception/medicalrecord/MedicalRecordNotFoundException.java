package deronzier.remi.safetynetalerts.exception.medicalrecord;

public class MedicalRecordNotFoundException extends Exception {

	private static final long serialVersionUID = -5506054790147166980L;

	public MedicalRecordNotFoundException() {
		super();
	}

	public MedicalRecordNotFoundException(String msg) {
		super(msg);
	}

	public MedicalRecordNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
