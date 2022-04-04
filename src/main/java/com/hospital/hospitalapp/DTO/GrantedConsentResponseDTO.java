package com.hospital.hospitalapp.DTO;

import lombok.Data;

@Data
public class GrantedConsentResponseDTO {
    private String patient_id;
    private String consent_id;
    private String delegateAccess;
    private String validity;

}
