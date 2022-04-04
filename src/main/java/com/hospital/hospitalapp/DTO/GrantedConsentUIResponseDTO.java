package com.hospital.hospitalapp.DTO;

import lombok.Data;

@Data
public class GrantedConsentUIResponseDTO {
    private String patient_id;
    private String patientName;
    private String consent_id;
    private String delegateAccess;
    private String validity;
}
