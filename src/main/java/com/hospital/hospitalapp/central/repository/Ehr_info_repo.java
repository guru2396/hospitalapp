package com.hospital.hospitalapp.central.repository;

import com.hospital.hospitalapp.central.entity.Ehr_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Ehr_info_repo  extends JpaRepository<Ehr_info,String> {
}
