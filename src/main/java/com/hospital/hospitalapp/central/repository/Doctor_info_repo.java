package com.hospital.hospitalapp.central.repository;

import com.hospital.hospitalapp.central.entity.Doctor_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface Doctor_info_repo extends JpaRepository<Doctor_info,String> {

    @Query(value = "SELECT doctor_email FROM doctor_info WHERE doctor_id=?1 AND doctor_email=?2",nativeQuery = true)
    public String findDoctor(String doctorId,String doctorEmail);


    @Query(value = "SELECT * FROM doctor_info WHERE doctor_id=?1",nativeQuery = true)
    Doctor_info getDoctorById(String doctorId);



    @Transactional
    @Modifying
    @Query(value = "UPDATE doctor_info SET doctor_name=?1, doctor_contact=?2, doctor_speciality=?3, doctor_password=?4 WHERE doctor_id=?5 AND doctor_email=?6",nativeQuery = true)
    Integer updateDoctorDetails(String doctorName,String doctorContact,String doctorSpeciality,String doctorPassword,String doctorId,String doctorEmail);

}
