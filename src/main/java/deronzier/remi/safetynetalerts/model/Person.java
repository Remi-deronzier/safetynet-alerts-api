package deronzier.remi.safetynetalerts.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Person implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String firstName;
	private final String lastName;
	private final String address;
	private final String city;
	private final String zip;
	private final String phone;
	private final String email;

}
