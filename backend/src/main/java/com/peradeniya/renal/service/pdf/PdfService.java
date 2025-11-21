package com.peradeniya.renal.service.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.peradeniya.renal.model.DischargeSummary;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateDischargeSummaryPdf(DischargeSummary ds) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 12);
            Font small = new Font(Font.FontFamily.HELVETICA, 10);

            // Header
            Paragraph header = new Paragraph("DISCHARGE SUMMARY", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            
            Paragraph hospital = new Paragraph("TEACHING HOSPITAL PERADENIYA", sectionFont);
            hospital.setAlignment(Element.ALIGN_CENTER);
            document.add(hospital);
            
            Paragraph unit = new Paragraph("RENAL CARE UNIT", sectionFont);
            unit.setAlignment(Element.ALIGN_CENTER);
            document.add(unit);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Patient details section
            Paragraph patientSection = new Paragraph("1. PATIENT DETAILS", sectionFont);
            patientSection.setSpacingAfter(10f);
            document.add(patientSection);

            document.add(new Paragraph("Name: " + safeString(ds.getPatient().getName()), normal));
            document.add(new Paragraph("PHN: " + safeString(ds.getPatient().getPhn()), normal));
            document.add(new Paragraph("BHT Number: " + safeString(ds.getAdmission().getBhtNumber()), normal));
            document.add(new Paragraph("Age: " + (ds.getPatient().getDob() != null ? calculateAge(ds.getPatient().getDob()) + " years" : "Not specified"), normal));
            document.add(new Paragraph("Sex: " + safeString(ds.getPatient().getSex()), normal));
            document.add(new Paragraph("Address: " + safeString(ds.getPatient().getAddress()), normal));
            document.add(new Paragraph("Admission Date: " + (ds.getAdmission().getAdmittedOn() != null ? ds.getAdmission().getAdmittedOn().toString() : "Not specified"), normal));
            document.add(new Paragraph("Discharge Date: " + (ds.getDischargeDate() != null ? ds.getDischargeDate().toString() : "Not specified"), normal));
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Diagnosis section
            Paragraph diagnosisSection = new Paragraph("2. DIAGNOSIS", sectionFont);
            diagnosisSection.setSpacingAfter(10f);
            document.add(diagnosisSection);
            
            document.add(new Paragraph("Final Diagnosis: " + safeString(ds.getDiagnosis()), normal));
            document.add(new Paragraph("ICD-10 Code: " + safeString(ds.getIcd10()), normal));
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Progress Summary
            if (ds.getProgressSummary() != null && !ds.getProgressSummary().trim().isEmpty()) {
                Paragraph progressSection = new Paragraph("3. PROGRESS SUMMARY", sectionFont);
                progressSection.setSpacingAfter(10f);
                document.add(progressSection);
                
                Paragraph progress = new Paragraph(safeString(ds.getProgressSummary()), normal);
                progress.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(progress);
                
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
            }

            // Management
            if (ds.getManagement() != null && !ds.getManagement().trim().isEmpty()) {
                Paragraph managementSection = new Paragraph("4. MANAGEMENT", sectionFont);
                managementSection.setSpacingAfter(10f);
                document.add(managementSection);
                
                Paragraph management = new Paragraph(safeString(ds.getManagement()), normal);
                management.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(management);
                
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
            }

            // Discharge Plan
            if (ds.getDischargePlan() != null && !ds.getDischargePlan().trim().isEmpty()) {
                Paragraph dischargePlanSection = new Paragraph("5. DISCHARGE PLAN", sectionFont);
                dischargePlanSection.setSpacingAfter(10f);
                document.add(dischargePlanSection);
                
                Paragraph dischargePlan = new Paragraph(safeString(ds.getDischargePlan()), normal);
                dischargePlan.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(dischargePlan);
                
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
            }

            // Drugs
            if (ds.getDrugsFreeHand() != null && !ds.getDrugsFreeHand().trim().isEmpty()) {
                Paragraph drugsSection = new Paragraph("6. DISCHARGE MEDICATIONS", sectionFont);
                drugsSection.setSpacingAfter(10f);
                document.add(drugsSection);
                
                Paragraph drugs = new Paragraph(safeString(ds.getDrugsFreeHand()), normal);
                drugs.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(drugs);
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            // Footer
            Paragraph footer = new Paragraph("Generated on: " + java.time.LocalDate.now(), small);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }
    }

    private String safeString(String value) {
        return value != null ? value : "Not specified";
    }
    
    private int calculateAge(java.time.LocalDate dob) {
        return java.time.Period.between(dob, java.time.LocalDate.now()).getYears();
    }
}