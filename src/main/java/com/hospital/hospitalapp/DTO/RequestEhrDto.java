package com.hospital.hospitalapp.DTO;

import lombok.Data;

import java.util.List;

@Data
public class RequestEhrDto {

    private String doctor_id;

    private String consent_id;

    private String patient_id;

    private String purpose;

    List<EpisodesDetails> episodes;
}
