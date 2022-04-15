package com.hospital.hospitalapp.repo;

import com.hospital.hospitalapp.entity.Doctor_login_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Doctor_login_info_repo extends JpaRepository<Doctor_login_info,String> {

    @Query(value = "SELECT * FROM doctor_login_info WHERE doctor_email=?1 and is_verified='Y'",nativeQuery = true)
    Doctor_login_info getLoginInfoByEmail(String email);

    @Query(value = "SELECT * FROM doctor_login_info WHERE doctor_id=?1",nativeQuery = true)
    Doctor_login_info getLoginInfoById(String id);
}
