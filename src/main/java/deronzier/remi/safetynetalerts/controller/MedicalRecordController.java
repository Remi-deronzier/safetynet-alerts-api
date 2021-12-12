package deronzier.remi.safetynetalerts.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
	 * Read - Get all medical records
	 * 
	 * @return - A list object of MedicalRecord full filled
	 * @throws IOException
	 * @throws DatabindException
	 * @throws StreamReadException
	 */
	@GetMapping("/medical-records")
	public List<MedicalRecord> getMedicalRecords() throws StreamReadException, DatabindException, IOException {
		return medicalRecordService.getMedicalRecords();
	}

}
