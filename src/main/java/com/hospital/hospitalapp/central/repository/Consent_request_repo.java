package com.hospital.hospitalapp.central.repository;

import com.hospital.hospitalapp.central.entity.Consent_request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Consent_request_repo extends JpaRepository<Consent_request,String> {
}
