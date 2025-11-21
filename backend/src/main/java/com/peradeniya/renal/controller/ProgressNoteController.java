package com.peradeniya.renal.controller;

import com.peradeniya.renal.dto.ProgressNoteRequest;
import com.peradeniya.renal.model.Admission;
import com.peradeniya.renal.model.Patient;
import com.peradeniya.renal.model.ProgressNote;
import com.peradeniya.renal.service.AdmissionService;
import com.peradeniya.renal.service.PatientService;
import com.peradeniya.renal.service.ProgressNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients/{phn}/admissions/{admId}/progress-notes")
@RequiredArgsConstructor
public class ProgressNoteController {

    private final PatientService patientService;
    private final AdmissionService admissionService;
    private final ProgressNoteService progressNoteService;

    @PostMapping
    public ProgressNote addNote(
            @PathVariable("phn") String phn,
            @PathVariable("admId") Long admId,
            @RequestBody ProgressNoteRequest req
    ) {
        System.out.println("Adding progress note for PHN: " + phn + ", Admission: " + admId);
        System.out.println("Progress note payload: " + req);
        
        String cleanPhn = phn.replaceAll("[^0-9]", "");
        Patient p = patientService.findByPhn(cleanPhn)
                .orElseThrow(() -> new RuntimeException("Patient not found with PHN: " + cleanPhn));

        Admission admission = admissionService.getById(admId);

        if (!admission.getPatient().getId().equals(p.getId()))
            throw new RuntimeException("Admission does not belong to patient");

        return progressNoteService.addNote(admission, req);
    }

    @GetMapping
    public List<ProgressNote> getNotes(
            @PathVariable("phn") String phn,
            @PathVariable("admId") Long admId
    ) {
        System.out.println("Getting progress notes for PHN: " + phn + ", Admission: " + admId);
        
        String cleanPhn = phn.replaceAll("[^0-9]", "");
        Patient p = patientService.findByPhn(cleanPhn)
                .orElseThrow(() -> new RuntimeException("Patient not found with PHN: " + cleanPhn));

        Admission admission = admissionService.getById(admId);

        if (!admission.getPatient().getId().equals(p.getId()))
            throw new RuntimeException("Admission does not belong to patient");

        return progressNoteService.getNotesForAdmission(admId);
    }
}