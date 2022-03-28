package com.hospital.hospitalapp.ehr.repository;

import com.hospital.hospitalapp.ehr.entity.Episodes_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Episodes_info_repo extends JpaRepository<Episodes_info,String> {

    @Query(value="SELECT episode_name FROM episodes_info WHERE episode_id=?1",nativeQuery = true)
    String getEpisodeNameById(String episodeId);

    @Query(value = "SELECT * FROM episodes_info WHERE ehr_id=?1",nativeQuery = true)
    List<Episodes_info> getEpisodesByEhrId(String ehr_id);
}
