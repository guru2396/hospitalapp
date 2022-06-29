package com.hospital.hospitalapp.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="op_record_info")
@Data
public class Op_Record_info {

    @Id
    private String op_record_id;

    private String encounter_id;

    private String diagnosis;

    private String record_details;

    private Date created_dt;
}
