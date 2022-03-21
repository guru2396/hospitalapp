package com.hospital.hospitalapp.service;

import com.hospital.hospitalapp.DTO.AuthRequestDTO;
import com.hospital.hospitalapp.DTO.ConsentRequestDTO;
import com.hospital.hospitalapp.DTO.GrantedConsentResponseDTO;
import com.hospital.hospitalapp.DTO.GrantedConsentUIResponseDTO;
import com.hospital.hospitalapp.central.entity.Consent_request;
import com.hospital.hospitalapp.central.entity.PatientInfo;
import com.hospital.hospitalapp.central.repository.Consent_request_repo;
import com.hospital.hospitalapp.central.repository.Patient_Info_Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class HospitalAppService {
    @Autowired
    private Consent_request_repo consent_request_repo;

    @Autowired
    private Patient_Info_Repo patient_info_repo;

    @Value("${hospital.id}")
    private String hospital_id;

    @Value("${consent.getGrantedConsents.url}")
    private String grantedConsentsURL;

    @Value("${consentManager.clientid}")
    private String client_id;

    @Value("${consentManager.clientSecret}")
    private String client_secret;

    @Value("${consentManager.authenticationURL}")
    private String authenticateURL;

    private String consentManagerToken;

    public boolean requestConsent(String doctor_id, ConsentRequestDTO consentreuestdto){

        Consent_request consent_request=new Consent_request();
        consent_request.setConsent_request_id("REQ_1234");
        consent_request.setPatient_id(consentreuestdto.getPatient_id());
        consent_request.setDoctor_id(doctor_id);
        System.out.println(hospital_id);
        consent_request.setHospital_id(hospital_id);
        consent_request.setRequest_info(consentreuestdto.getRequest_info());
        consent_request.setAccess_purpose(consentreuestdto.getAccess_purpose());
        consent_request.setRequest_status("Pending");
        consent_request.setCreated_dt(new Date());

        try{
            consent_request_repo.save(consent_request);

        }catch(Exception e){
            System.out.println(e);
            return false;
        }
        return true;
    }


    public List<GrantedConsentUIResponseDTO> getGrantedConsents(String doctor_id){
        RestTemplate restTemplate=new RestTemplate();
        String doctor_idURL=grantedConsentsURL+ doctor_id;
        if(consentManagerToken==null)
        {
            AuthRequestDTO authRequestDTO=new AuthRequestDTO();
            authRequestDTO.setUsername(client_id);
            authRequestDTO.setPassword(client_secret);

            HttpHeaders httpHeaders=new HttpHeaders();
            HttpEntity<?> httpEntity=new HttpEntity<>(authRequestDTO,httpHeaders);
            ResponseEntity<String> tokenResponse=restTemplate.exchange(authenticateURL, HttpMethod.POST,httpEntity,String.class);
            consentManagerToken=tokenResponse.getBody();
        }
        HttpHeaders httpHeaders=new HttpHeaders();
        HttpEntity<?> httpEntity=new HttpEntity<>(httpHeaders);
        String finalToken="Bearer " + consentManagerToken;
        List<String> tokens=new ArrayList<>();
        tokens.add(finalToken);
        httpHeaders.put("Authorization",tokens);
        ResponseEntity<List<GrantedConsentResponseDTO>> grantedConsentSet=restTemplate.exchange(doctor_idURL, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<GrantedConsentResponseDTO>>() {
        });
        List<GrantedConsentResponseDTO> grantedConsentResponseDTOS=grantedConsentSet.getBody();

        List<GrantedConsentUIResponseDTO> grantedConsents=new ArrayList<>();

        for (GrantedConsentResponseDTO grantedConsent:
                grantedConsentResponseDTOS) {
            GrantedConsentUIResponseDTO grantedConsentUIResponseDTO=new GrantedConsentUIResponseDTO();
            grantedConsentUIResponseDTO.setPatient_id(grantedConsent.getPatient_id());
            PatientInfo patient=patient_info_repo.getPatientNames(grantedConsent.getPatient_id());
            grantedConsentUIResponseDTO.setPatientName(patient.getPatient_name());
            grantedConsentUIResponseDTO.setConsent_id(grantedConsent.getConsent_id());
            grantedConsentUIResponseDTO.setDelegateAcess(grantedConsent.getDelegateAcess());
            grantedConsentUIResponseDTO.setValidity(grantedConsent.getValidity());
            grantedConsents.add(grantedConsentUIResponseDTO);
        }
        return grantedConsents;
    }
}
