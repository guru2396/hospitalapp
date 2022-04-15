package com.hospital.hospitalapp.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="encounter_info")
@Data
public class Encounter_info {

    @Id
    private String encounter_id;

    private String episode_id;

    private String doctor_id;

    private String patient_id;

    private Date created_dt;
}
