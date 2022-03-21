package com.hospital.hospitalapp.DTO;

import lombok.Data;

import java.util.List;

@Data
public class EpisodesDTO {
    private String episode_id;
    private String episode_name;
    List<EncountersDTO> encountersDTOList;
}
