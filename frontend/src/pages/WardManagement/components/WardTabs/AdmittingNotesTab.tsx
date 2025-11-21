import React from 'react';
import { Patient } from '../../types/wardManagement';
import ReadField from '../ReadField';

interface AdmittingNotesTabProps {
  patient: Patient;
}

const AdmittingNotesTab: React.FC<AdmittingNotesTabProps> = ({ patient }) => {
  // Format admission time to display only time part
  const formatAdmissionTime = (dateTimeString?: string): string => {
    if (!dateTimeString) return "–";
    try {
      const date = new Date(dateTimeString);
      return date.toLocaleTimeString('en-US', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: true 
      });
    } catch {
      return "–";
    }
  };

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
      <h3 className="mb-4 text-sm font-semibold text-slate-800">
        Admitting Notes
      </h3>
      <div className="grid grid-cols-1 gap-4 text-xs md:grid-cols-2">
        <ReadField
          label="Type of Admission"
          value={patient.admissionType || "–"}
        />
        <ReadField
          label="Admitting Officer"
          value={patient.admittingOfficer || "–"}
        />
        <ReadField
          label="Admitting Hospital"
          value={patient.referredBy || "Teaching Hospital Peradeniya"}
        />
        <ReadField
          label="Ward / Bed"
          value={`${patient.wardNumber || patient.ward} / ${
            patient.bedId || "-"
          }`}
        />
        <ReadField label="Date of Admission" value={patient.admissionDate || "–"} />
        <ReadField label="Time of Admission" value={formatAdmissionTime(patient.admissionTime) || "–"} />
      </div>
      <div className="mt-4">
        <label className="mb-1 block text-[11px] font-medium text-slate-600">
          Presenting Complaints
        </label>
        <textarea
          readOnly
          className="w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-xs text-slate-800"
          value={patient.presentingComplaints || "No presenting complaints recorded"}
        />
      </div>

      {/* Examinations */}
      <div className="mt-4">
        <h4 className="mb-2 text-xs font-semibold text-slate-700">
          Examinations at Admission
        </h4>
        <div className="grid grid-cols-1 gap-3 text-xs md:grid-cols-3">
          <ReadField
            label="Temperature (°C)"
            value={patient.examTempC?.toString() || "–"}
          />
          <ReadField
            label="Height (cm)"
            value={patient.examHeightCm?.toString() || "–"}
          />
          <ReadField
            label="Body Weight (kg)"
            value={patient.examWeightKg?.toString() || "–"}
          />
          <ReadField
            label="BMI (kg/m²)"
            value={patient.examBMI ? patient.examBMI.toFixed(1) : "–"}
          />
          <ReadField
            label="Blood Pressure (mmHg)"
            value={patient.examBloodPressure || "–"}
          />
          <ReadField
            label="Heart Rate (bpm)"
            value={patient.examHeartRate?.toString() || "–"}
          />
        </div>
      </div>
    </div>
  );
};

export default AdmittingNotesTab;