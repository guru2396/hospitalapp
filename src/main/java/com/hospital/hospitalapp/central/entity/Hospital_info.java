package com.hospital.hospitalapp.central.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="hospital_info")
@Data
public class Hospital_info {

    @Id
    private String hospital_id;

    private String hospital_name;

    private String hospital_address;
}
