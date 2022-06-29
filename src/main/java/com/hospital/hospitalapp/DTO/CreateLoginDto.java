package com.hospital.hospitalapp.DTO;

import lombok.Data;

@Data
public class CreateLoginDto {

    private String doctor_id;

    private String username;

    private String password;
}
