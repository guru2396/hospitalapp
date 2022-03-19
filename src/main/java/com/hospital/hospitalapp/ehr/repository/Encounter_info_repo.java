package com.hospital.hospitalapp.ehr.repository;

import com.hospital.hospitalapp.ehr.entity.Encounter_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Encounter_info_repo extends JpaRepository<Encounter_info,String> {
}
