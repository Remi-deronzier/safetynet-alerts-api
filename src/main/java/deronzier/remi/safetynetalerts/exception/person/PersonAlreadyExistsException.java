package deronzier.remi.safetynetalerts.exception.person;

public class PersonAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 2319768476093112645L;

	public PersonAlreadyExistsException() {
		super();
	}

	public PersonAlreadyExistsException(String msg) {
		super(msg);
	}

	public PersonAlreadyExistsException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
