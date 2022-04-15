package com.hospital.hospitalapp.DTO;

import lombok.Data;

import javax.persistence.Column;

@Data
public class PatientDto {

    private String patient_id;

    private String patient_name;

    private String patient_contact;

    private String patient_address;

    private String patient_gender;

    private String patient_email;

    private String patient_emergency_contact_name;

    private String patient_emergency_contact;

    private String patient_dob;

    private String patient_govtid_type;

    private String patient_govtid;
}
