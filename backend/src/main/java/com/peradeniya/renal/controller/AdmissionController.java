package com.peradeniya.renal.controller;

import com.peradeniya.renal.model.Admission;
import com.peradeniya.renal.model.Patient;
import com.peradeniya.renal.service.AdmissionService;
import com.peradeniya.renal.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients/{phn}/admissions")
@RequiredArgsConstructor
public class AdmissionController {

    private final PatientService patientService;
    private final AdmissionService admissionService;

    @GetMapping
    public ResponseEntity<List<Admission>> getAdmissions(@PathVariable("phn") String phn) {
        String cleanPhn = phn.replaceAll("[^0-9]", "");
        
        Patient patient = patientService.findByPhn(cleanPhn)
                .orElseThrow(() -> new RuntimeException("Patient not found with PHN: " + cleanPhn));

        // FIXED: Use correct method name
        List<Admission> admissions = admissionService.getAdmissionsForPatient(patient);
        return ResponseEntity.ok(admissions);
    }
}