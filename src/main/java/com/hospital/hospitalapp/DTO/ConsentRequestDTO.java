package com.hospital.hospitalapp.DTO;

import lombok.Data;

@Data
public class ConsentRequestDTO {
    private String patient_id;

    private String request_info;

    private String access_purpose;

}
