package deronzier.remi.safetynetalerts.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import deronzier.remi.safetynetalerts.exception.AddressNotFound;
import deronzier.remi.safetynetalerts.exception.medicalrecord.MedicalRecordAlreadyExistsException;
import deronzier.remi.safetynetalerts.exception.medicalrecord.MedicalRecordNotFoundException;
import deronzier.remi.safetynetalerts.exception.person.PersonNotFoundException;
import deronzier.remi.safetynetalerts.model.CreateClass;
import deronzier.remi.safetynetalerts.model.UpdateClass;
import deronzier.remi.safetynetalerts.model.medicalrecord.MedicalRecord;
import deronzier.remi.safetynetalerts.service.MedicalRecordService;

@RestController
public class MedicalRecordController {

	@Autowired
	private MedicalRecordService medicalRecordService;

	/**
	 * @param address
	 * @return List of all family members living at this specific address
	 */
	@GetMapping("/childAlert")
	public Map<String, Object> getPersonsCoveredFireStation(@RequestParam final String address) {
		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> children = (List<Map<String, Object>>) medicalRecordService
					.getChildrenSpecificAddress(address).get("children");
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> adults = (List<Map<String, Object>>) medicalRecordService
					.getChildrenSpecificAddress(address).get("adults");
			if (children.isEmpty() && adults.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No family member found");
			}
			return medicalRecordService.getChildrenSpecificAddress(address);
		} catch (AddressNotFound anf) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, anf.getMessage());
		}

	}

	// CRUD

	/**
	 * @return List of all medical records
	 */
	@GetMapping("/medical-records")
	public List<MedicalRecord> findAll() {
		if (medicalRecordService.finAll().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical record found");
		}
		return medicalRecordService.finAll();
	}

	/**
	 * @param newMedicalRecord
	 * @param result
	 * @return new medical record created
	 */
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping("/medical-records")
	MedicalRecord create(@Validated(CreateClass.class) @RequestBody MedicalRecord newMedicalRecord,
			BindingResult result) {
		if (result.hasErrors()) {
			List<String> errorMessages = new ArrayList<>();
			result.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
			String errorMessage = String.join(", ", errorMessages);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
		}
		try {
			return medicalRecordService.save(newMedicalRecord);
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		} catch (MedicalRecordAlreadyExistsException mraee) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, mraee.getMessage());
		} catch (PersonNotFoundException pnfe) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, pnfe.getMessage());
		}
	}

	/**
	 * @param firstName
	 * @param lastName
	 * @return A message to warn whether the medical record has been deleted
	 *         successfully
	 */
	@DeleteMapping("/medical-records")
	String delete(@RequestParam final String firstName, final String lastName) {
		try {
			return medicalRecordService.delete(firstName, lastName);
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		} catch (MedicalRecordNotFoundException mrnfe) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, mrnfe.getMessage());
		}
	}

	/**
	 * @param medicalRecord
	 * @param firstName
	 * @param lastName
	 * @return Updated medical record
	 */
	@PutMapping("/medical-records")
	MedicalRecord update(@RequestParam final String firstName, final String lastName,
			@Validated(UpdateClass.class) @RequestBody MedicalRecord medicalRecord, BindingResult result) {
		if (medicalRecord.getFirstName() != null || medicalRecord.getLastName() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name and last name cannot be modified");
		}
		if (result.hasErrors()) {
			List<String> errorMessages = new ArrayList<>();
			result.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
			String errorMessage = String.join(", ", errorMessages);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
		}
		try {
			return medicalRecordService.update(medicalRecord, firstName, lastName);
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage());
		} catch (MedicalRecordNotFoundException mrnfe) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, mrnfe.getMessage());
		}
	}

}
