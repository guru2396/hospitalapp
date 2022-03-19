package com.hospital.hospitalapp.central.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="consent_request")
@Data
public class Consent_request {

    @Id
    private String consent_request_id;

    private String patient_id;

    private String doctor_id;

    private String hospital_id;

    private String request_info;

    private String access_purpose;

    private String request_status;

    private Date created_dt;

}
