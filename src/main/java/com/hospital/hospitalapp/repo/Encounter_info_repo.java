package com.hospital.hospitalapp.repo;


import com.hospital.hospitalapp.entity.Encounter_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Encounter_info_repo extends JpaRepository<Encounter_info,String> {

    @Query(value = "SELECT * FROM encounter_info WHERE encounter_id=?1",nativeQuery = true)
    Encounter_info getEncounterById(String encounterId);

    @Query(value = "SELECT * FROM encounter_info WHERE episode_id=?1",nativeQuery = true)
    List<Encounter_info> getEncountersByEpisodeId(String episodeId);

}
