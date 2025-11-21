package com.peradeniya.renal.dto;

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

    // Admission merged fields
    private String bhtNumber;
    private String ward;
    private String wardNumber;
    private String bedId;
    private LocalDate admissionDate;
    private String admissionTime;
    private String consultantName;
    private String referredBy;
    private String primaryDiagnosis;
    private String admissionType;
    private String admittingOfficer;
    private String presentingComplaints;

    // Examination fields from Admission
    private Double examTempC;
    private Double examHeightCm;
    private Double examWeightKg;
    private Double examBMI;
    private String examBloodPressure;
    private Integer examHeartRate;

    // NEW: Include admission ID and active status for frontend
    private Long admissionId;
    private Boolean hasActiveAdmission;

    // Remove @AllArgsConstructor and manually create the constructor we need
    public PatientResponse(
            Long id, String phn, String name, LocalDate dob, String sex, String status,
            String address, String phone, String nic, String mohArea, String ethnicGroup, 
            String religion, String occupation, String maritalStatus,
            String bhtNumber, String ward, String wardNumber, String bedId, 
            LocalDate admissionDate, String admissionTime, String consultantName, 
            String referredBy, String primaryDiagnosis, String admissionType, 
            String admittingOfficer, String presentingComplaints,
            Double examTempC, Double examHeightCm, Double examWeightKg, Double examBMI, 
            String examBloodPressure, Integer examHeartRate,
            Long admissionId, Boolean hasActiveAdmission) {
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
        this.bhtNumber = bhtNumber;
        this.ward = ward;
        this.wardNumber = wardNumber;
        this.bedId = bedId;
        this.admissionDate = admissionDate;
        this.admissionTime = admissionTime;
        this.consultantName = consultantName;
        this.referredBy = referredBy;
        this.primaryDiagnosis = primaryDiagnosis;
        this.admissionType = admissionType;
        this.admittingOfficer = admittingOfficer;
        this.presentingComplaints = presentingComplaints;
        this.examTempC = examTempC;
        this.examHeightCm = examHeightCm;
        this.examWeightKg = examWeightKg;
        this.examBMI = examBMI;
        this.examBloodPressure = examBloodPressure;
        this.examHeartRate = examHeartRate;
        this.admissionId = admissionId;
        this.hasActiveAdmission = hasActiveAdmission;
    }

    public static PatientResponse from(Patient p, Admission a) {
        return new PatientResponse(
                p.getId(),
                p.getPhn(),
                p.getName(),
                p.getDob(),
                p.getSex(),
                p.getStatus(),

                // Patient demographics
                p.getAddress(),
                p.getPhone(),
                p.getNic(),
                p.getMohArea(),
                p.getEthnicGroup(),
                p.getReligion(),
                p.getOccupation(),
                p.getMaritalStatus(),

                // Admission fields
                a != null ? a.getBhtNumber() : null,
                a != null ? a.getWard() : null,
                a != null ? a.getWardNumber() : null,
                a != null ? a.getBedId() : null,
                a != null ? a.getAdmittedOn() : null,
                a != null && a.getAdmissionTime() != null ? 
                    a.getAdmissionTime().format(DateTimeFormatter.ofPattern("HH:mm")) : null,
                a != null ? a.getConsultantName() : null,
                a != null ? a.getReferredBy() : null,
                a != null ? a.getPrimaryDiagnosis() : null,
                a != null ? a.getAdmissionType() : null,
                a != null ? a.getAdmittingOfficer() : null,
                a != null ? a.getPresentingComplaints() : null,

                // Examination fields
                a != null ? a.getExamTempC() : null,
                a != null ? a.getExamHeightCm() : null,
                a != null ? a.getExamWeightKg() : null,
                a != null ? a.getExamBMI() : null,
                a != null ? a.getExamBloodPressure() : null,
                a != null ? a.getExamHeartRate() : null,
                
                // NEW: Admission ID and active status
                a != null ? a.getId() : null,
                a != null && a.isActive()
        );
    }
}