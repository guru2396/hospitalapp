package com.hospital.hospitalapp.central.repository;

import com.hospital.hospitalapp.central.entity.Hospital_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Hospital_info_repo extends JpaRepository<Hospital_info,String> {
}
