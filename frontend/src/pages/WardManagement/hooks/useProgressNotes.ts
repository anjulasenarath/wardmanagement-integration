import { useState } from 'react';
import { Patient, Admission, ProgressForm } from '../types/wardManagement';
import { apiAddProgressNote } from '../services/api';

export const useProgressNotes = (patient: Patient | null, admissions: Admission[]) => {
  const [progressForm, setProgressForm] = useState<ProgressForm>({
    tempC: "",
    weightKg: "",
    bpHigh: "",
    bpLow: "",
    heartRate: "",
    inputMl: "",
    urineOutputMl: "",
    pdBalance: "",
    totalBalance: "",
  });

  const [progressNotes, setProgressNotes] = useState<any[]>([]);

  const handleProgressChange =
    (field: keyof ProgressForm) =>
    (e: React.ChangeEvent<HTMLInputElement>) => {
      setProgressForm((prev) => ({ ...prev, [field]: e.target.value }));
    };

  const handleSubmitProgress = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!patient) {
      alert("No patient selected");
      return;
    }

    const activeAdmission = admissions.find((a) => a.isActive);
    if (!activeAdmission) {
      alert("No active admission found");
      return;
    }

    // Validate at least one field is filled
    const hasData = Object.values(progressForm).some(value => value.trim() !== "");
    if (!hasData) {
      alert("Please fill in at least one field");
      return;
    }

    const payload = {
      tempC: progressForm.tempC ? Number(progressForm.tempC) : undefined,
      weightKg: progressForm.weightKg ? Number(progressForm.weightKg) : undefined,
      bpHigh: progressForm.bpHigh ? Number(progressForm.bpHigh) : undefined,
      bpLow: progressForm.bpLow ? Number(progressForm.bpLow) : undefined,
      heartRate: progressForm.heartRate ? Number(progressForm.heartRate) : undefined,
      inputMl: progressForm.inputMl ? Number(progressForm.inputMl) : undefined,
      urineOutputMl: progressForm.urineOutputMl
        ? Number(progressForm.urineOutputMl)
        : undefined,
      pdBalance: progressForm.pdBalance ? Number(progressForm.pdBalance) : undefined,
      totalBalance: progressForm.totalBalance
        ? Number(progressForm.totalBalance)
        : undefined,
    };

    try {
      const newNote = await apiAddProgressNote(
        patient.phn,
        activeAdmission.id,
        payload
      );
      setProgressNotes((prev) => [newNote, ...prev]);

      // Reset form
      setProgressForm({
        tempC: "",
        weightKg: "",
        bpHigh: "",
        bpLow: "",
        heartRate: "",
        inputMl: "",
        urineOutputMl: "",
        pdBalance: "",
        totalBalance: "",
      });

      alert("Progress note saved successfully!");
    } catch (error) {
      console.error("Error adding progress note:", error);
      alert("Failed to add progress note: " + error.message);
    }
  };

  return {
    progressForm,
    progressNotes,
    handleProgressChange,
    handleSubmitProgress
  };
};