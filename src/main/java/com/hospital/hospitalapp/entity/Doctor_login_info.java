package com.hospital.hospitalapp.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="doctor_login_info")
@Data
public class Doctor_login_info {

    @Id
    private String doctor_id;

    private String doctor_name;

    private String doctor_email;

    private String doctor_password;

    private String is_verified;
}
