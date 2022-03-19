package com.hospital.hospitalapp.central.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="ehr_info")
@Data
public class Ehr_info {

    @Id
    private String ehr_id;

    private String patient_id;

    private Date created_dt;
}
