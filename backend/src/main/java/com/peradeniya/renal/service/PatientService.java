package com.peradeniya.renal.service;

import com.peradeniya.renal.dto.PatientCreateRequest;
import com.peradeniya.renal.mapper.PatientMapper;
import com.peradeniya.renal.model.Admission;
import com.peradeniya.renal.model.Patient;
import com.peradeniya.renal.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper mapper;
    private final AdmissionService admissionService;

    public Patient createPatient(PatientCreateRequest request) {
        // Check if patient already exists
        Optional<Patient> existingPatient = findByPhn(request.getPhn());
        if (existingPatient.isPresent()) {
            throw new RuntimeException("Patient with PHN " + request.getPhn() + " already exists");
        }

        // Create patient
        Patient patient = mapper.toPatient(request);
        Patient savedPatient = patientRepository.save(patient);

        // Create admission
        Admission admission = admissionService.createAdmission(savedPatient, request);
        System.out.println("✅ Created admission: " + admission.getId() + " for patient: " + savedPatient.getPhn());
        System.out.println("✅ Admission active status: " + admission.isActive());

        return savedPatient;
    }

    public Optional<Patient> findByPhn(String phn) {
        String cleanPhn = phn.replaceAll("[^0-9]", "");
        System.out.println("🔍 Searching for patient with cleaned PHN: " + cleanPhn);
        return patientRepository.findByPhn(cleanPhn);
    }

    public Patient updatePatientStatus(String phn, String status) {
        String cleanPhn = phn.replaceAll("[^0-9]", "");
        Patient patient = findByPhn(cleanPhn)
                .orElseThrow(() -> new RuntimeException("Patient not found with PHN: " + cleanPhn));
        
        patient.setStatus(status);
        return patientRepository.save(patient);
    }

    public Optional<Admission> getActiveAdmission(Patient patient) {
        System.out.println("🔍 Looking for active admission for patient: " + patient.getPhn());
        Optional<Admission> admissionOpt = admissionService.getActiveAdmission(patient);
        
        if (admissionOpt.isPresent()) {
            System.out.println("✅ Found active admission: " + admissionOpt.get().getId());
        } else {
            System.out.println("❌ No active admission found for patient: " + patient.getPhn());
            // Let's check if there are any admissions at all
            var allAdmissions = admissionService.getAdmissionsForPatient(patient);
            System.out.println("📋 Total admissions for patient: " + allAdmissions.size());
            allAdmissions.forEach(adm -> System.out.println("   - Admission " + adm.getId() + ", active: " + adm.isActive()));
        }
        
        return admissionOpt;
    }
    
   public Patient getPatientByPhn(String phn) {
    String cleanPhn = phn.replaceAll("[^0-9]", "");
    System.out.println("🔍 PATIENT SERVICE: Getting patient by PHN: " + cleanPhn);
    
    Optional<Patient> patientOpt = findByPhn(cleanPhn);
    
    if (patientOpt.isPresent()) {
        Patient patient = patientOpt.get();
        System.out.println("✅ PATIENT SERVICE: Found patient - ID: " + patient.getId() + ", Name: " + patient.getName());
        System.out.println("✅ PATIENT SERVICE: Patient status: " + patient.getStatus());
        System.out.println("✅ PATIENT SERVICE: Total admissions: " + patient.getAdmissions().size());
        
        // Log all admissions
        patient.getAdmissions().forEach(adm -> {
            System.out.println("   📋 Admission ID: " + adm.getId() + ", Active: " + adm.isActive() + ", BHT: " + adm.getBhtNumber());
        });
        
        return patient;
    } else {
        System.out.println("❌ PATIENT SERVICE: No patient found with PHN: " + cleanPhn);
        throw new RuntimeException("Patient not found with PHN: " + cleanPhn);
    }
}
}