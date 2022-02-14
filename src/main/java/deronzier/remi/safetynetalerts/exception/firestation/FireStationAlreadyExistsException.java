package deronzier.remi.safetynetalerts.exception.firestation;

public class FireStationAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 5107121389008311149L;

	public FireStationAlreadyExistsException() {
		super();
	}

	public FireStationAlreadyExistsException(String msg) {
		super(msg);
	}

	public FireStationAlreadyExistsException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
