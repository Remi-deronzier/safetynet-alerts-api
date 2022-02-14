package deronzier.remi.safetynetalerts.exception.person;

public class PersonNotFoundException extends Exception {

	private static final long serialVersionUID = 6366421205388461610L;

	public PersonNotFoundException() {
		super();
	}

	public PersonNotFoundException(String msg) {
		super(msg);
	}

	public PersonNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
