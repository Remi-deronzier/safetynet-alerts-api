package deronzier.remi.safetynetalerts.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MedicalRecord implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String firstName;
	private final String lastName;
	private final String birthday;
	private final List<String> medications;
	private final List<String> allergies;

}
