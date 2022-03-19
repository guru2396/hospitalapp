package com.hospital.hospitalapp.service;

import com.hospital.hospitalapp.DTO.ConsentRequestDTO;
import com.hospital.hospitalapp.central.entity.Consent_request;
import com.hospital.hospitalapp.central.repository.Consent_request_repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class HospitalAppService {
    @Autowired
    private Consent_request_repo consent_request_repo;

    @Value("{Hospital.id}")
    private String hospital_id;

    public boolean requestConsent(String doctor_id, ConsentRequestDTO consentreuestdto){

        Consent_request consent_request=new Consent_request();
        consent_request.setConsent_request_id(UUID.randomUUID().toString());
        consent_request.setPatient_id(consentreuestdto.getPatient_id());
        consent_request.setDoctor_id(doctor_id);
        consent_request.setHospital_id(hospital_id);
        consent_request.setRequest_info(consentreuestdto.getRequest_info());
        consent_request.setAccess_purpose(consentreuestdto.getAccess_purpose());
        consent_request.setRequest_status("Pending");
        consent_request.setCreated_dt(new Date());

        try{
            consent_request_repo.save(consent_request);

        }catch(Exception e){
            return false;
        }
        return true;
    }
}
