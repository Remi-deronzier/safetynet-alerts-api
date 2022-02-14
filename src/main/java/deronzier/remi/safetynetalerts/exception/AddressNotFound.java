package deronzier.remi.safetynetalerts.exception;

public class AddressNotFound extends Exception {

	private static final long serialVersionUID = -7139304880555402679L;

	public AddressNotFound() {
		super();
	}

	public AddressNotFound(String msg) {
		super(msg);
	}

	public AddressNotFound(String msg, Throwable cause) {
		super(msg, cause);
	}

}
