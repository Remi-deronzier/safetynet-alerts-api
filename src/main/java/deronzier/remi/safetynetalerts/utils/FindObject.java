package deronzier.remi.safetynetalerts.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import deronzier.remi.safetynetalerts.model.Identity;

@Component
public class FindObject<T extends Identity> {

	public boolean findPerson(String firstName, String lastName, T identity) {
		return identity.getFirstName().equalsIgnoreCase(firstName) && identity.getLastName().equalsIgnoreCase(lastName);
	}

	public T findPerson(String firstName, String lastName, List<T> identities) {
		return identities
				.stream()
				.filter(identity -> findPerson(firstName, lastName, identity))
				.findFirst()
				.orElse(null);
	}

	public int indexOfPerson(String firstName, String lastName, List<T> identities) {
		return identities.indexOf(findPerson(firstName, lastName, identities));
	}

}
