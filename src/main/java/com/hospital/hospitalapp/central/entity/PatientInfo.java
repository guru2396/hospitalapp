package com.hospital.hospitalapp.central.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="patient_info")
@Data
public class PatientInfo {

    @Id
    @Column(name="patient_id")
    private String patient_id;

    @Column(name="patient_name")
    private String patient_name;

    @Column(name="patient_contact")
    private String patient_contact;

    @Column(name="patient_address")
    private String patient_address;

    @Column(name="patient_gender")
    private String patient_gender;

    @Column(name="patient_email")
    private String patient_email;

    @Column(name="patient_emergency_contact_name")
    private String patient_emergency_contact_name;

    @Column(name="patient_emergency_contact")
    private String patient_emergency_contact;

    @Column(name="patient_dob")
    private String patient_dob;

    @Column(name="patient_govtid_type")
    private String patient_govtid_type;

    @Column(name="patient_govtid")
    private String patient_govtid;

    @Column(name="patient_password")
    private String patient_password;
}
