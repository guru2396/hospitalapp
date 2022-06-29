package com.hospital.hospitalapp.DTO;

import lombok.Data;

import java.util.List;
@Data
public class EpisodesDetails {
    private String episodeId;
    private String time_limit_records;
    List<EncounterDetails> encounterDetails;
}
