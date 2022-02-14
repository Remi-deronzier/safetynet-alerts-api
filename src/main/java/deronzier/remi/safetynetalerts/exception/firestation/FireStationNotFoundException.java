package deronzier.remi.safetynetalerts.exception.firestation;

public class FireStationNotFoundException extends Exception {

	private static final long serialVersionUID = 5093736681926400195L;

	public FireStationNotFoundException() {
		super();
	}

	public FireStationNotFoundException(String msg) {
		super(msg);
	}

	public FireStationNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
