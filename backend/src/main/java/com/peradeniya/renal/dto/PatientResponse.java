package com.peradeniya.renal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peradeniya.renal.model.Admission;
import com.peradeniya.renal.model.Patient;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class PatientResponse {

    private Long id;
    private String phn;
    private String name;
    private LocalDate dob;
    private String sex;
    private String status;

    // Patient demographics
    private String address;
    private String phone;
    private String nic;
    private String mohArea;
    private String ethnicGroup;
    private String religion;
    private String occupation;
    private String maritalStatus;

    // IMPORTANT: frontend expects this:
    @JsonProperty("activeAdmission")
    private AdmissionResponse activeAdmission;

    public PatientResponse() {}

    public PatientResponse(
            Long id, String phn, String name, LocalDate dob, String sex, String status,
            String address, String phone, String nic, String mohArea, String ethnicGroup,
            String religion, String occupation, String maritalStatus,
            AdmissionResponse activeAdmission
    ) {
        this.id = id;
        this.phn = phn;
        this.name = name;
        this.dob = dob;
        this.sex = sex;
        this.status = status;
        this.address = address;
        this.phone = phone;
        this.nic = nic;
        this.mohArea = mohArea;
        this.ethnicGroup = ethnicGroup;
        this.religion = religion;
        this.occupation = occupation;
        this.maritalStatus = maritalStatus;
        this.activeAdmission = activeAdmission;
    }

    public static PatientResponse from(Patient p, Admission a) {

        AdmissionResponse activeAdmission = null;

        if (a != null) {
            activeAdmission = new AdmissionResponse(
                    a.getId(),
                    a.getBhtNumber(),
                    a.getNumber(),
                    a.isActive(),
                    a.isDischargeSummaryAvailable(),

                    a.getAdmittedOn(),
                    a.getAdmissionTime() != null ?
                            a.getAdmissionTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                            : null,

                    a.getWard(),
                    a.getWardNumber(),
                    a.getBedId(),

                    a.getConsultantName(),
                    a.getReferredBy(),
                    a.getPrimaryDiagnosis(),
                    a.getAdmissionType(),
                    a.getAdmittingOfficer(),
                    a.getPresentingComplaints(),

                    a.getExamTempC(),
                    a.getExamHeightCm(),
                    a.getExamWeightKg(),
                    a.getExamBMI(),
                    a.getExamBloodPressure(),
                    a.getExamHeartRate()
            );
        }

        return new PatientResponse(
                p.getId(),
                p.getPhn(),
                p.getName(),
                p.getDob(),
                p.getSex(),
                p.getStatus(),
                p.getAddress(),
                p.getPhone(),
                p.getNic(),
                p.getMohArea(),
                p.getEthnicGroup(),
                p.getReligion(),
                p.getOccupation(),
                p.getMaritalStatus(),
                activeAdmission
        );
    }
}
