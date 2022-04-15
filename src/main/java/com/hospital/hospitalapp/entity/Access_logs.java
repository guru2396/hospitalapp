package com.hospital.hospitalapp.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="access_logs")
@Data
public class Access_logs {

    @Id
    private String access_log_id;

    private String patient_id;

    private String doctor_id;

    private String consent_id;

    private String ehr_id;

    private String records_accessed;

    private Date created_dt;
}
