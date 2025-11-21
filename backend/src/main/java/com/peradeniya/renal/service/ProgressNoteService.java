package com.peradeniya.renal.service;

import com.peradeniya.renal.dto.ProgressNoteRequest;
import com.peradeniya.renal.model.Admission;
import com.peradeniya.renal.model.ProgressNote;
import com.peradeniya.renal.repository.ProgressNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressNoteService {

    private final ProgressNoteRepository progressNoteRepository;

    public ProgressNote addNote(Admission admission, ProgressNoteRequest req) {
        System.out.println("Saving progress note for admission: " + admission.getId());
        System.out.println("Progress note data: " + req);
        
        ProgressNote note = new ProgressNote();
        note.setAdmission(admission);
        note.setTempC(req.getTempC());
        note.setWeightKg(req.getWeightKg());
        note.setBpHigh(req.getBpHigh());
        note.setBpLow(req.getBpLow());
        note.setHeartRate(req.getHeartRate());
        note.setInputMl(req.getInputMl());
        note.setUrineOutputMl(req.getUrineOutputMl());
        note.setPdBalance(req.getPdBalance());
        note.setTotalBalance(req.getTotalBalance());
        
        ProgressNote savedNote = progressNoteRepository.save(note);
        System.out.println("Progress note saved with ID: " + savedNote.getId());
        
        return savedNote;
    }

    public List<ProgressNote> getNotesForAdmission(Long admissionId) {
        System.out.println("Fetching progress notes for admission: " + admissionId);
        return progressNoteRepository.findByAdmissionIdOrderByCreatedAtDesc(admissionId);
    }
}