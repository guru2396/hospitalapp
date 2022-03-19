package com.hospital.hospitalapp.central.repository;

import com.hospital.hospitalapp.central.entity.Doctor_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Doctor_info_repo extends JpaRepository<Doctor_info,String> {
}
