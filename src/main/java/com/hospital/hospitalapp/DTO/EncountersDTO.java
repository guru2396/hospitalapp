package com.hospital.hospitalapp.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EncountersDTO {
    List<Ops_recordsDTO> ops_recordsDTOList;
    private String encounter_id;
}
