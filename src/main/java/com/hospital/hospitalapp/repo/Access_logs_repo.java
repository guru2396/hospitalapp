package com.hospital.hospitalapp.repo;

import com.hospital.hospitalapp.entity.Access_logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Access_logs_repo extends JpaRepository<Access_logs,String> {

    @Query(value = "SELECT * FROM access_logs WHERE patient_id=?1",nativeQuery = true)
    List<Access_logs> getAccessLogsForPatient(String patientId);

}
