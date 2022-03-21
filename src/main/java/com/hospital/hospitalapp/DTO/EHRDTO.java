package com.hospital.hospitalapp.DTO;

import lombok.Data;

import java.util.List;

@Data
public class EHRDTO {
private String ehr_id;
List<EpisodesDTO> episodesDTOList;
}
