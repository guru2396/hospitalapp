package com.hospital.hospitalapp.central.repository;

import com.hospital.hospitalapp.central.entity.PatientInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Patient_Info_Repo extends JpaRepository<PatientInfo,String> {
    @Query(value = "SELECT * FROM patient_info where patient_id=?1",nativeQuery = true)
    public PatientInfo getPatientNames(String patient_id);
}
