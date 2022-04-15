package com.hospital.hospitalapp.DTO;

import lombok.Data;

@Data
public class AccessLogDto {

    private String log_id;

    private String doctor_name;

    private String hospital_name;

    private String consent_id;

    //private String data_custodian_id;

    private String access_details;

    private String access_purpose;

    private String timestamp;
}
