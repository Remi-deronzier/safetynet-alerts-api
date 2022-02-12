package deronzier.remi.safetynetalerts.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import deronzier.remi.safetynetalerts.model.MedicalRecord;
import deronzier.remi.safetynetalerts.service.MedicalRecordService;

@RestController
public class MedicalRecordController {

	@Autowired
	private MedicalRecordService medicalRecordService;

	/**
	 * @return A list of all medical records
	 * @throws StreamReadException
	 * @throws DatabindException
	 * @throws IOException
	 */
	@GetMapping("/medical-records")
	public List<MedicalRecord> getMedicalRecords() throws StreamReadException, DatabindException, IOException {
		return medicalRecordService.getMedicalRecords();
	}

	/**
	 * @param address
	 * @return List of children and adults that live at specific address
	 */
	@GetMapping("/childAlert")
	public Map<String, Object> getPersonsCoveredFireStation(@RequestParam final String address) {
		return medicalRecordService.getChildrenSpecificAddress(address);

	}

}
