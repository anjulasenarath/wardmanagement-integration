package com.peradeniya.renal.controller;

import com.peradeniya.renal.dto.PatientCreateRequest;
import com.peradeniya.renal.dto.PatientResponse;
import com.peradeniya.renal.model.Admission;
import com.peradeniya.renal.model.Patient;
import com.peradeniya.renal.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody PatientCreateRequest request) {
        Patient patient = patientService.createPatient(request);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<PatientResponse> getPatientByPhn(@RequestParam("phn") String phn) {

        String cleanPhn = phn.replaceAll("[^0-9]", "");

        Patient patient = patientService.getPatientByPhn(cleanPhn);
        Optional<Admission> admissionOpt = patientService.getActiveAdmission(patient);
        Admission admission = admissionOpt.orElse(null);

        PatientResponse response = PatientResponse.from(patient, admission);

        // Safe debug
        System.out.println("activeAdmission exists? " + (response.getActiveAdmission() != null));
        System.out.println("activeAdmission ID: " +
                (response.getActiveAdmission() != null ? response.getActiveAdmission().getId() : null));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{phn}/status")
    public ResponseEntity<Patient> updatePatientStatus(
            @PathVariable("phn") String phn,
            @RequestBody StatusUpdateRequest request) {

        String cleanPhn = phn.replaceAll("[^0-9]", "");
        Patient patient = patientService.updatePatientStatus(cleanPhn, request.getStatus());
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/debug/{phn}")
    public ResponseEntity<Map<String, Object>> debugPatient(@PathVariable("phn") String phn) {

        String cleanPhn = phn.replaceAll("[^0-9]", "");
        try {
            Patient patient = patientService.getPatientByPhn(cleanPhn);
            Optional<Admission> admissionOpt = patientService.getActiveAdmission(patient);
            Admission admission = admissionOpt.orElse(null);

            PatientResponse response = PatientResponse.from(patient, admission);

            Map<String, Object> debug = new HashMap<>();
            debug.put("patient", patient);
            debug.put("activeAdmission", response.getActiveAdmission());
            debug.put("activeAdmissionId",
                    response.getActiveAdmission() != null ? response.getActiveAdmission().getId() : null);

            return ResponseEntity.ok(debug);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    public static class StatusUpdateRequest {
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
