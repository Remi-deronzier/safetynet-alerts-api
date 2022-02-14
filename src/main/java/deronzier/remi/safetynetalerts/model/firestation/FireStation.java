package deronzier.remi.safetynetalerts.model.firestation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import deronzier.remi.safetynetalerts.model.CreateClass;
import deronzier.remi.safetynetalerts.model.UpdateClass;
import lombok.Data;

@Data
public class FireStation {

	@NotBlank(message = "Address cannot be null", groups = CreateClass.class)
	private String address;

	@NotNull(message = "Station number cannot be null", groups = { CreateClass.class, UpdateClass.class })
	@Positive(message = "Station number must be positive", groups = { CreateClass.class, UpdateClass.class })
	private int station;

}
