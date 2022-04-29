package com.hospital.hospitalapp.DTO;

import lombok.Data;

@Data
public class DoctorLoginRequestDto {

    private String doctor_id;

    private String doctor_name;

    private String doctor_email;
}
