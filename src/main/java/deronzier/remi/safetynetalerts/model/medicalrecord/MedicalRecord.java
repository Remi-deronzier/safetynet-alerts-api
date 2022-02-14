package deronzier.remi.safetynetalerts.model.medicalrecord;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.fasterxml.jackson.annotation.JsonFormat;

import deronzier.remi.safetynetalerts.model.CreateClass;
import deronzier.remi.safetynetalerts.model.Identity;
import deronzier.remi.safetynetalerts.model.UpdateClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MedicalRecord extends Identity {

	@NotBlank(message = "First name cannot be null", groups = CreateClass.class)
	private String firstName;

	@NotBlank(message = "Last name cannot be null", groups = CreateClass.class)
	private String lastName;

	@NotNull(message = "Birthday cannot be null", groups = { CreateClass.class, UpdateClass.class })
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyy")
	@Past(message = "Birthday must be in the past", groups = { CreateClass.class, UpdateClass.class })
	private Date birthdate;

	@NotNull(message = "Medications cannot be null", groups = { CreateClass.class, UpdateClass.class })
	private List<String> medications;

	@NotNull(message = "Allergies cannot be null", groups = { CreateClass.class, UpdateClass.class })
	private List<String> allergies;

}
