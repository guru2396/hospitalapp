package com.hospital.hospitalapp.ehr.repository;

import com.hospital.hospitalapp.ehr.entity.Episodes_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Episodes_info_repo extends JpaRepository<Episodes_info,String> {
}
