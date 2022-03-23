package com.hospital.hospitalapp.service;

import com.hospital.hospitalapp.DTO.*;
import com.hospital.hospitalapp.central.entity.Consent_request;
import com.hospital.hospitalapp.central.entity.PatientInfo;
import com.hospital.hospitalapp.central.repository.Consent_request_repo;
import com.hospital.hospitalapp.central.repository.Patient_Info_Repo;
import com.hospital.hospitalapp.ehr.entity.Op_Record_info;
import com.hospital.hospitalapp.ehr.repository.Episodes_info_repo;
import com.hospital.hospitalapp.ehr.repository.Op_Record_info_repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
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

    @Value("${consentManager.validate}")
    private  String validateConsentURL;

    @Autowired
    private Episodes_info_repo episodes_info_repo;

    @Autowired
    private Op_Record_info_repo op_record_info_repo;

    private String consentManagerToken;

    private String getToken(){
        if(consentManagerToken==null)
        {
            RestTemplate restTemplate=new RestTemplate();
            AuthRequestDTO authRequestDTO=new AuthRequestDTO();
            authRequestDTO.setUsername(client_id);
            authRequestDTO.setPassword(client_secret);

            HttpHeaders httpHeaders=new HttpHeaders();
            HttpEntity<?> httpEntity=new HttpEntity<>(authRequestDTO,httpHeaders);
            ResponseEntity<String> tokenResponse=restTemplate.exchange(authenticateURL, HttpMethod.POST,httpEntity,String.class);
            consentManagerToken=tokenResponse.getBody();
        }
        return consentManagerToken;
    }

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
        String consentToken=getToken();
        HttpHeaders httpHeaders=new HttpHeaders();
        HttpEntity<?> httpEntity=new HttpEntity<>(httpHeaders);
        String finalToken="Bearer " + consentToken;
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

    public EHRDTO getEHR(String consent_id,String patient_id,String doctor_id) {
        String consentToken = getToken();
//        ValidateCMDTO cm =new ValidateCMDTO();
//        cm.setConsent_id(consent_id);
//        cm.setDoctor_id(doctor_id);
//        cm.setPatient_id(patient_id);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        String finalToken = "Bearer " + consentToken;
        List<String> tokens = new ArrayList<>();
        tokens.add(finalToken);
        httpHeaders.put("Authorization", tokens);
        ResponseEntity<ValidateConsentDTO> validateConsent = restTemplate.exchange(validateConsentURL+"/"+consent_id, HttpMethod.POST, httpEntity, ValidateConsentDTO.class);
        EHRDTO ehrdto=new EHRDTO();
        ValidateConsentDTO validateConsentDTO=validateConsent.getBody();
        List<EpisodesDTO> episodesDTOList=new ArrayList<>();
        for(EpisodesDetails episodesDetails:validateConsentDTO.getEpisodes()){
            EpisodesDTO episodesDTO=new EpisodesDTO();
            episodesDTO.setEpisode_id(episodesDetails.getEpisodeId());
            episodesDTO.setEpisode_name(episodes_info_repo.getEpisodeNameById(episodesDetails.getEpisodeId()));
            List<EncountersDTO> encountersDTOList=new ArrayList<>();
            System.out.print("Encounter:"+episodesDetails.getEncounterDetails().size());
            for(EncounterDetails encounterDetails:episodesDetails.getEncounterDetails()){
                EncountersDTO encountersDTO=new EncountersDTO();
                encountersDTO.setEncounter_id(encounterDetails.getEncounterId());
                List<Op_Record_info> ops_recordsList=op_record_info_repo.getOpRecords(encounterDetails.getEncounterId());
                System.out.println("Db list size:"+ops_recordsList.size());
                List<Ops_recordsDTO> ops_recordsDTOList=new ArrayList<>();
                for(Op_Record_info op_record_info:ops_recordsList){
                    Ops_recordsDTO ops_recordsDTO=new Ops_recordsDTO();
                    ops_recordsDTO.setOp_record_id(op_record_info.getOp_record_id());
                    ops_recordsDTO.setDiagnosis(op_record_info.getDiagnosis());
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    String date=null;
                    date=simpleDateFormat.format(op_record_info.getCreated_dt());
                    ops_recordsDTO.setTimestamp(date);
                    ops_recordsDTO.setRecord_details(op_record_info.getRecord_details());
                    ops_recordsDTOList.add(ops_recordsDTO);
                }
                encountersDTO.setOps_recordsDTOList(ops_recordsDTOList);
                encountersDTOList.add(encountersDTO);
            }
            episodesDTO.setEncountersDTOList(encountersDTOList);
            episodesDTOList.add(episodesDTO);
        }
        ehrdto.setEpisodesDTOList(episodesDTOList);
        return ehrdto;
    }
}
