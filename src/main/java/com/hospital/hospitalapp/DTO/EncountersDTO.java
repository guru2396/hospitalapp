package com.hospital.hospitalapp.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EncountersDTO {
    private List<Ops_recordsDTO> op_records;
    private String encounterId;
    private String doctorName;
}
