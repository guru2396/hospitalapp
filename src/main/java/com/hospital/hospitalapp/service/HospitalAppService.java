package com.hospital.hospitalapp.service;

import com.hospital.hospitalapp.DTO.*;
import com.hospital.hospitalapp.central.entity.Consent_request;
import com.hospital.hospitalapp.central.entity.Doctor_info;
import com.hospital.hospitalapp.central.entity.PatientInfo;
import com.hospital.hospitalapp.central.repository.Consent_request_repo;
import com.hospital.hospitalapp.central.repository.Doctor_info_repo;
import com.hospital.hospitalapp.central.repository.Ehr_info_repo;
import com.hospital.hospitalapp.central.repository.Patient_Info_Repo;
import com.hospital.hospitalapp.ehr.entity.Access_logs;
import com.hospital.hospitalapp.ehr.entity.Encounter_info;
import com.hospital.hospitalapp.ehr.entity.Episodes_info;
import com.hospital.hospitalapp.ehr.entity.Op_Record_info;
import com.hospital.hospitalapp.ehr.repository.Access_logs_repo;
import com.hospital.hospitalapp.ehr.repository.Encounter_info_repo;
import com.hospital.hospitalapp.ehr.repository.Episodes_info_repo;
import com.hospital.hospitalapp.ehr.repository.Op_Record_info_repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private Doctor_info_repo doctor_info_repo;

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

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    private Episodes_info_repo episodes_info_repo;

    @Autowired
    private Op_Record_info_repo op_record_info_repo;

    @Autowired
    private Encounter_info_repo encounter_info_repo;

    @Autowired
    private Ehr_info_repo ehr_info_repo;

    private String consentManagerToken;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Access_logs_repo access_logs_repo;

    private PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

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
        long id=generateID();
        String consentRequestId="REQ_" + id;
        consent_request.setConsent_request_id(consentRequestId);
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
            episodesDTO.setEpisodeId(episodesDetails.getEpisodeId());
            episodesDTO.setEpisodeName(episodes_info_repo.getEpisodeNameById(episodesDetails.getEpisodeId()));
            List<EncountersDTO> encountersDTOList=new ArrayList<>();
            System.out.print("Encounter:"+episodesDetails.getEncounterDetails().size());
            for(EncounterDetails encounterDetails:episodesDetails.getEncounterDetails()){
                EncountersDTO encountersDTO=new EncountersDTO();
                encountersDTO.setEncounterId(encounterDetails.getEncounterId());
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
                    ops_recordsDTO.setRecordDetails(op_record_info.getRecord_details());
                    ops_recordsDTOList.add(ops_recordsDTO);
                }
                encountersDTO.setOp_records(ops_recordsDTOList);
                String doctorId=encounter_info_repo.getEncounterById(encounterDetails.getEncounterId()).getDoctor_id();
                String doctorName=doctor_info_repo.getDoctorById(doctorId).getDoctor_name();
                encountersDTO.setDoctorName(doctorName);
                encountersDTOList.add(encountersDTO);
            }
            episodesDTO.setEncounters(encountersDTOList);
            episodesDTOList.add(episodesDTO);
        }
        ehrdto.setEpisodesDTOList(episodesDTOList);
        saveAccessLog(consent_id,patient_id,doctor_id);
        return ehrdto;
    }

    private void saveAccessLog(String consent_id,String patient_id,String doctor_id){
        Access_logs access_logs=new Access_logs();
        access_logs.setPatient_id(patient_id);
        access_logs.setDoctor_id(doctor_id);
        access_logs.setCreated_dt(new Date());
        long id=generateID();
        String access_log_id="Acc_" + id;
        access_logs.setAccess_log_id(access_log_id);
        access_logs_repo.save(access_logs);
    }

    public List<EpisodesDTO> fetchEntireEhrOfPatient(String patientId){
        List<EpisodesDTO> episodes=new ArrayList<>();
        String ehr_id=ehr_info_repo.getEhrIdByPatientId(patientId);
        List<Episodes_info> episodes_infos= episodes_info_repo.getEpisodesByEhrId(ehr_id);
        if(episodes_infos!=null){
            for(Episodes_info episodes_info:episodes_infos){
                System.out.println("Episodes");
                EpisodesDTO episodesDTO=new EpisodesDTO();
                episodesDTO.setEpisodeId(episodes_info.getEpisode_id());
                episodesDTO.setEpisodeName(episodes_info.getEpisode_name());
                List<Encounter_info> encounter_infos=encounter_info_repo.getEncountersByEpisodeId(episodes_info.getEpisode_id());
                List<EncountersDTO> encounters=new ArrayList<>();
                if(encounter_infos!=null){
                    for(Encounter_info encounter_info:encounter_infos){
                        System.out.println("Encounters");
                        EncountersDTO encountersDTO=new EncountersDTO();
                        encountersDTO.setEncounterId(encounter_info.getEncounter_id());
                        String doctorName=doctor_info_repo.getDoctorById(encounter_info.getDoctor_id()).getDoctor_name();
                        encountersDTO.setDoctorName(doctorName);
                        List<Ops_recordsDTO> op_records=new ArrayList<>();
                        List<Op_Record_info> op_record_infos=op_record_info_repo.getOpRecords(encounter_info.getEncounter_id());
                        if(op_record_infos!=null){
                            for(Op_Record_info op_record_info:op_record_infos){
                                System.out.println("OP records");
                                Ops_recordsDTO ops_recordsDTO=new Ops_recordsDTO();
                                ops_recordsDTO.setOp_record_id(op_record_info.getOp_record_id());
                                ops_recordsDTO.setDiagnosis(op_record_info.getDiagnosis());
                                ops_recordsDTO.setRecordDetails(op_record_info.getRecord_details());
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                                String date=simpleDateFormat.format(op_record_info.getCreated_dt());
                                ops_recordsDTO.setTimestamp(date);
                                op_records.add(ops_recordsDTO);
                            }
                        }
                        encountersDTO.setOp_records(op_records);
                        encounters.add(encountersDTO);
                    }
                }
                episodesDTO.setEncounters(encounters);
                episodes.add(episodesDTO);
            }
        }
        return episodes;
    }

    public PatientInfo getPatientById(String patientId){
        return patient_info_repo.getPatientNames(patientId);
    }

    public Doctor_info getDoctorById(String doctorId){
        return doctor_info_repo.getDoctorById(doctorId);
    }



    public String loginAdmin(AdminLoginDTO adminLoginDto) {

        String email = adminLoginDto.getAdmin_email();
        String password = adminLoginDto.getAdmin_password();
        System.out.println(email + " " + password);
        if (email.equals(adminUsername) && password.equals(adminPassword)) {
            //String patient_id = patient_info_repo.findId(email);
            String token = jwtService.createToken(adminUsername);
            return token;
        } else {
            /*
            Unmatched is returned if passwords are not matched.This is used as a key to know whether passwords matched or not
            Donot change the returned value
            */
            return null;
        }
    }

    public String addDoctor(DoctorRegistrationDTO doctorRegistrationDto){


        String email = doctorRegistrationDto.getDoctor_email();

        //We have to check whether the admin is adding or someone else is adding the data
        Doctor_info doctor=doctor_info_repo.getDoctorByEmail(email);
        if(doctor==null){
            System.out.println(email);
            Doctor_info doctor_info = new Doctor_info();
            doctor_info.setDoctor_email(doctorRegistrationDto.getDoctor_email());

            long id=generateID();
            String doctorId="DOC_" + id;
            doctor_info.setDoctor_id(doctorId); // String id="PAT_"+UUID.randomUUID().toString();
            doctor_info_repo.save(doctor_info);

            return doctor_info.getDoctor_id();
        }
        else{
            return null;
        }

    }



    public String registerDoctor(DoctorRegistrationDTO doctorRegistrationDto){
        Doctor_info doctor_info=new Doctor_info();
        String id = doctorRegistrationDto.getDoctor_id();
        String email = doctorRegistrationDto.getDoctor_email();

        //check if the doctor with the entered id and email exist or not
        String ret_email = doctor_info_repo.findDoctor(id,email);
        if(email.equals(ret_email)){
            //doctor with the id exist hence we can now procede updating the values
            String name = doctorRegistrationDto.getDoctor_name();
            String contact = doctorRegistrationDto.getDoctor_contact();
            String speciality = doctorRegistrationDto.getDoctor_speciality();
            String hash_password = passwordEncoder.encode(doctorRegistrationDto.getDoctor_password());
             //saving hashed password in database;

            doctor_info_repo.updateDoctorDetails(name,contact,speciality,hash_password,id,email);
            return "Success";
        }
        else{
            return null;
        }


    }

    public String loginDoctor(AuthRequestDTO authRequestDTO){
        Doctor_info doctor_info=doctor_info_repo.getDoctorByEmail(authRequestDTO.getUsername());
        if(doctor_info!=null){
            boolean isMatch=passwordEncoder.matches(authRequestDTO.getPassword(),doctor_info.getDoctor_password());
            if(isMatch){
                String token=jwtService.createToken(doctor_info.getDoctor_id());
                return token;
            }
            return null;
        }
        return null;
    }

    public long generateID(){
        long id=(long) Math.floor(Math.random()*9_000_000_000L)+1_000_000_000L;
        return id;
    }


}
