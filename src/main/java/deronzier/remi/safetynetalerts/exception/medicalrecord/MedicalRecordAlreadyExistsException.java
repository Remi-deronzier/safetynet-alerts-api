package deronzier.remi.safetynetalerts.exception.medicalrecord;

public class MedicalRecordAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 5863517226425066451L;

	public MedicalRecordAlreadyExistsException() {
		super();
	}

	public MedicalRecordAlreadyExistsException(String msg) {
		super(msg);
	}

	public MedicalRecordAlreadyExistsException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
