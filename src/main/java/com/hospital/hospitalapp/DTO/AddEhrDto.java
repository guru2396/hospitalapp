package com.hospital.hospitalapp.DTO;

import lombok.Data;

@Data
public class AddEhrDto {

    private String patient_id;

    private String episode;

    private String episode_name;

    private String diagnosis;

    private String complaints;

    private String treatment;

    private String prescription;

    private String followUpPlan;
}
