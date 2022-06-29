package com.hospital.hospitalapp.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="episodes_info")
@Data
public class Episodes_info {

    @Id
    private String episode_id;

    private String ehr_id;

    private String episode_name;

    private String episode_code;

    private Date created_dt;
}
