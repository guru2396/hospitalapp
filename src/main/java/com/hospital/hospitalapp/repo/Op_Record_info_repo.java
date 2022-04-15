package com.hospital.hospitalapp.repo;


import com.hospital.hospitalapp.entity.Op_Record_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Op_Record_info_repo extends JpaRepository<Op_Record_info,String> {

    @Query(value = "SELECT * FROM op_record_info where encounter_id=?1",nativeQuery = true)
    List<Op_Record_info> getOpRecords(String encounterId);
}
