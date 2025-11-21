import React, { useState } from "react";
import { X } from "lucide-react";
import { PatientCreatePayload, Sex, AdmissionType } from "../../types/wardManagement";
import IdentitySection from "./components/PatientFormSections/IdentitySection";
import ContactSection from "./components/PatientFormSections/ContactSection";
import AdmissionSection from "./components/PatientFormSections/AdmissionSection";
import ExaminationSection from "./components/PatientFormSections/ExaminationSection";
import ProblemListSection from "./components/PatientFormSections/ProblemListSection";
import ProblemListTab from "../ProblemListTab";



;


type PatientForm = {
  phn: string;
  bht: string;
  name: string;
  dob: string;
  sex: Sex;
  phone: string;
  nic: string;
  address: string;
  mohArea: string;
  ethnicGroup: string;
  religion: string;
  occupation: string;
  maritalStatus: string;
  ward: string;
  wardNumber: string;
  bedId: string;
  admissionDate: string;
  admissionTime: string;
  consultantName: string;
  referredBy: string;
  primaryDiagnosis: string;
  admissionType: AdmissionType | "";
  admittingOfficer: string;
  presentingComplaints: string;
  examTempC: string;
  examHeightCm: string;
  examWeightKg: string;
  examBloodPressure: string;
  examHeartRate: string;
  problemList: string[];
};

type ValidationErrors = Partial<Record<keyof PatientForm, string>>;

interface AddPatientModalProps {
  onClose: () => void;
  onCreate: (payload: PatientCreatePayload) => void;
}

const AddPatientModal: React.FC<AddPatientModalProps> = ({ onClose, onCreate }) => {
  const [form, setForm] = useState<PatientForm>({
    phn: "",
    bht: "",
    name: "",
    dob: "",
    sex: "Male",
    phone: "",
    nic: "",
    address: "",
    mohArea: "",
    ethnicGroup: "",
    religion: "",
    occupation: "",
    maritalStatus: "",
    ward: "",
    wardNumber: "",
    bedId: "",
    admissionDate: "",
    admissionTime: "",
    consultantName: "",
    referredBy: "",
    primaryDiagnosis: "",
    admissionType: "",
    admittingOfficer: "",
    presentingComplaints: "",
    examTempC: "",
    examHeightCm: "",
    examWeightKg: "",
    examBloodPressure: "",
    examHeartRate: "",
    problemList: [""],
  });

  const [errors, setErrors] = useState<ValidationErrors>({});

  const handleChange = (field: keyof PatientForm) => (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const value = e.target.value;
    setForm((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: undefined }));
  };

  const handleProblemChange = (index: number) => (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const copy = [...form.problemList];
    copy[index] = e.target.value;
    setForm((prev) => ({ ...prev, problemList: copy }));
  };

  const addProblemBox = () => {
    if (form.problemList.length < 10) {
      setForm((prev) => ({
        ...prev,
        problemList: [...prev.problemList, ""],
      }));
    }
  };

  const validate = (): boolean => {
    const errs: ValidationErrors = {};

    const requiredFields: (keyof PatientForm)[] = [
      "phn",
      "name",
      "dob",
      "sex",
      "ward",
      "admissionDate",
      "admissionType",
      "presentingComplaints",
    ];

    requiredFields.forEach((f) => {
      if (!form[f] || (typeof form[f] === "string" && !form[f].trim())) {
        errs[f] = "Required";
      }
    });

    if (form.phone && !/^\d{10}$/.test(form.phone)) {
      errs.phone = "Phone must be exactly 10 digits";
    }

    const numericFields: (keyof PatientForm)[] = [
      "examTempC",
      "examHeightCm",
      "examWeightKg",
      "examHeartRate",
    ];
    numericFields.forEach((f) => {
      const val = form[f];
      if (val && isNaN(Number(val))) {
        errs[f] = "Must be a number";
      }
    });

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    // ⭐ FIX 1: Correct ISO LocalDate
    const isoDate =
      form.admissionDate && !isNaN(Date.parse(form.admissionDate))
        ? new Date(form.admissionDate).toISOString().split("T")[0]
        : undefined;

    // ⭐ FIX 2: Correct ISO LocalDateTime for time
    let isoDateTime: string | undefined;
    if (form.admissionTime && isoDate) {
      isoDateTime = `${isoDate}T${form.admissionTime}:00`;
    }

    // ⭐ FIX 3: BMI calculation
    const bmi =
      form.examHeightCm && form.examWeightKg
        ? (() => {
            const h = Number(form.examHeightCm);
            const w = Number(form.examWeightKg);
            if (!h || !w) return undefined;
            const m = h / 100;
            return Number((w / (m * m)).toFixed(1));
          })()
        : undefined;

    // ⭐ FIX 4: FINAL payload — matches backend EXACTLY
    const payload: PatientCreatePayload = {
      phn: form.phn.trim(),
      name: form.name.trim(),
      dob: form.dob,
      sex: form.sex,

      address: form.address || undefined,
      phone: form.phone || undefined,
      nic: form.nic || undefined,
      mohArea: form.mohArea || undefined,
      ethnicGroup: form.ethnicGroup || undefined,
      religion: form.religion || undefined,
      occupation: form.occupation || undefined,
      maritalStatus: form.maritalStatus || undefined,

      ward: form.ward,
      wardNumber: form.wardNumber || undefined,
      bedId: form.bedId || undefined,
      admissionDate: isoDate,
      admissionTime: isoDateTime,
      admissionType: form.admissionType || "Other",
      consultantName: form.consultantName || undefined,
      referredBy: form.referredBy || undefined,
      primaryDiagnosis: form.primaryDiagnosis || undefined,
      admittingOfficer: form.admittingOfficer || undefined,
      presentingComplaints: form.presentingComplaints,

      tempC: form.examTempC ? Number(form.examTempC) : undefined,
      heightCm: form.examHeightCm ? Number(form.examHeightCm) : undefined,
      weightKg: form.examWeightKg ? Number(form.examWeightKg) : undefined,
      bmi: bmi,
      bloodPressure: form.examBloodPressure || undefined,
      heartRate: form.examHeartRate ? Number(form.examHeartRate) : undefined,

      medicalProblems: form.problemList
        .map((p) => p.trim())
        .filter((p) => p.length > 0),
      allergyProblems: [],
    };

    console.log("Submitting payload:", payload);
    onCreate(payload);
  };

  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center bg-black/40">
      <div className="relative w-full max-w-5xl max-h-[90vh] overflow-hidden rounded-2xl bg-white shadow-lg">
        <header className="flex items-center justify-between border-b border-slate-200 bg-slate-50 px-4 py-2">
          <div>
            <h3 className="text-sm font-semibold text-slate-800">Add New Patient</h3>
            <p className="text-[11px] text-slate-500">
              This will create a new PHN/BHT admission. All fields go through basic
              validation.
            </p>
          </div>
          <button
            onClick={onClose}
            className="inline-flex h-7 w-7 items-center justify-center rounded-full border border-slate-300 bg-white text-slate-600 hover:bg-slate-100"
          >
            <X className="h-3.5 w-3.5" />
          </button>
        </header>

        <form
          onSubmit={handleSubmit}
          className="max-h-[calc(90vh-40px)] overflow-y-auto px-4 py-3 text-xs"
        >
          <IdentitySection form={form} errors={errors} onChange={handleChange} />
          <ContactSection form={form} errors={errors} onChange={handleChange} />
          <AdmissionSection form={form} errors={errors} onChange={handleChange} />
          <ExaminationSection form={form} errors={errors} onChange={handleChange} />
        <ProblemListTab
  title="Problem List"
  helpText="Add up to 10 medical problems"
  problems={form.problemList}
  setProblems={(newList) =>
    setForm((prev) => ({ ...prev, problemList: newList }))
  }
/>

          <div className="mt-4 flex items-center justify-end gap-2 border-t border-slate-200 pt-3">
            <button
              type="button"
              onClick={onClose}
              className="inline-flex items-center rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-[11px] font-medium text-slate-700 hover:bg-slate-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="inline-flex items-center rounded-lg bg-blue-600 px-4 py-1.5 text-[11px] font-medium text-white hover:bg-blue-700"
            >
              Save Patient
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddPatientModal;
