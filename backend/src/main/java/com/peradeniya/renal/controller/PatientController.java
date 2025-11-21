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
        System.out.println("Received patient creation request: " + request);
        
        // Log admission time for debugging
        if (request.getAdmissionTime() != null) {
            System.out.println("Admission time from request: " + request.getAdmissionTime());
            System.out.println("Parsed admission time: " + request.getAdmissionTimeAsLocalDateTime());
        }
        
        Patient patient = patientService.createPatient(request);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<PatientResponse> getPatientByPhn(@RequestParam("phn") String phn) {
        String cleanPhn = phn.replaceAll("[^0-9]", "");
        System.out.println("Searching for patient with PHN: " + cleanPhn);
        
        // Use the new method that throws exception if not found
        Patient patient = patientService.getPatientByPhn(cleanPhn);
        
        // Get active admission (returns Optional)
        Optional<Admission> admissionOpt = patientService.getActiveAdmission(patient);
        Admission admission = admissionOpt.orElse(null);
        
        PatientResponse response = PatientResponse.from(patient, admission);
        
        // Debug logging
        System.out.println("✅ PatientResponse created - hasActiveAdmission: " + response.getHasActiveAdmission());
        System.out.println("✅ PatientResponse created - admissionId: " + response.getAdmissionId());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{phn}/status")
    public ResponseEntity<Patient> updatePatientStatus(
            @PathVariable("phn") String phn, 
            @RequestBody StatusUpdateRequest request) {
        String cleanPhn = phn.replaceAll("[^0-9]", "");
        System.out.println("Updating status for PHN: " + cleanPhn + " to: " + request.getStatus());
        
        Patient patient = patientService.updatePatientStatus(cleanPhn, request.getStatus());
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/debug/{phn}")
    public ResponseEntity<Map<String, Object>> debugPatient(@PathVariable("phn") String phn) {
        String cleanPhn = phn.replaceAll("[^0-9]", "");
        System.out.println("🔍 DEBUG: Getting patient with PHN: " + cleanPhn);
        
        try {
            Patient patient = patientService.getPatientByPhn(cleanPhn);
            Optional<Admission> admissionOpt = patientService.getActiveAdmission(patient);
            Admission admission = admissionOpt.orElse(null);
            
            PatientResponse response = PatientResponse.from(patient, admission);
            
            Map<String, Object> debug = new HashMap<>();
            debug.put("patient", patient);
            debug.put("admission", admission);
            debug.put("response", response);
            debug.put("hasActiveAdmission", admission != null && admission.isActive());
            debug.put("admissionId", admission != null ? admission.getId() : null);
            
            System.out.println("✅ DEBUG: Response hasActiveAdmission: " + response.getHasActiveAdmission());
            System.out.println("✅ DEBUG: Response admissionId: " + response.getAdmissionId());
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // DTO for status update - with manual getter/setter
    public static class StatusUpdateRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @GetMapping("/test-search")
public ResponseEntity<Map<String, Object>> testSearch(@RequestParam("phn") String phn) {
    String cleanPhn = phn.replaceAll("[^0-9]", "");
    System.out.println("🧪 TEST SEARCH: PHN = " + cleanPhn);
    
    Map<String, Object> response = new HashMap<>();
    
    try {
        // Check if patient exists
        Optional<Patient> patientOpt = patientService.findByPhn(cleanPhn);
        
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            Optional<Admission> admissionOpt = patientService.getActiveAdmission(patient);
            
            response.put("found", true);
            response.put("patientId", patient.getId());
            response.put("patientName", patient.getName());
            response.put("hasActiveAdmission", admissionOpt.isPresent());
            response.put("admissionCount", patient.getAdmissions().size());
            
            if (admissionOpt.isPresent()) {
                response.put("admissionId", admissionOpt.get().getId());
                response.put("admissionActive", admissionOpt.get().isActive());
            }
        } else {
            response.put("found", false);
            response.put("message", "Patient not found");
        }
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        response.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
}