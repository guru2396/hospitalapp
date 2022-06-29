package com.hospital.hospitalapp.DTO;

import lombok.Data;

import java.util.List;

@Data
public class EpisodesDTO {
    private String episodeId;
    private String episodeName;
    List<EncountersDTO> encounters;
}
