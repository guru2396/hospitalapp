package com.hospital.hospitalapp.DTO;

import lombok.Data;

@Data
public class DoctorRegistrationDTO {
    private String doctor_id;
    private String doctor_name;
    private String doctor_email;
    private String doctor_contact;
    private String doctor_speciality;
    private String doctor_password;

}
