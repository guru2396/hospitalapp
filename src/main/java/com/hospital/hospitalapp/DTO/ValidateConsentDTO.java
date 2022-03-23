package com.hospital.hospitalapp.DTO;

import lombok.Data;

import java.util.List;
@Data
public class ValidateConsentDTO {
    private String dataCustodianId;
    List<EpisodesDetails> episodes;
}
