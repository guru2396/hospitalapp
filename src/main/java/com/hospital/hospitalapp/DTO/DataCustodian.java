package com.hospital.hospitalapp.DTO;

import lombok.Data;

import java.util.List;

@Data
public class DataCustodian {
    private String dataCustodianId;

    private List<EpisodesDetails> episodes;
}
