package deronzier.remi.safetynetalerts.model.person;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import deronzier.remi.safetynetalerts.model.CreateClass;
import deronzier.remi.safetynetalerts.model.Identity;
import deronzier.remi.safetynetalerts.model.UpdateClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Person extends Identity {

	@NotBlank(message = "First name cannot be null", groups = CreateClass.class)
	private String firstName;

	@NotBlank(message = "Last name cannot be null", groups = CreateClass.class)
	private String lastName;

	@NotBlank(message = "Addresse cannot be null", groups = { CreateClass.class, UpdateClass.class })
	private String address;

	@NotBlank(message = "City cannot be null", groups = { CreateClass.class, UpdateClass.class })
	private String city;

	@NotBlank(message = "Zip cannot be null", groups = { CreateClass.class, UpdateClass.class })
	private String zip;

	@NotBlank(message = "Phone cannot be null", groups = { CreateClass.class, UpdateClass.class })
	private String phone;

	@NotBlank(message = "Email cannot be null", groups = { CreateClass.class, UpdateClass.class })
	@Email(message = "Email should be valid", groups = { CreateClass.class, UpdateClass.class })
	private String email;

}
