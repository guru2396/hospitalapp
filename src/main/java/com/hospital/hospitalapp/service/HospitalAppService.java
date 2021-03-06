package com.hospital.hospitalapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.hospitalapp.DTO.*;
import com.hospital.hospitalapp.entity.*;
import com.hospital.hospitalapp.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HospitalAppService {

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


    private String consentManagerToken;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Access_logs_repo access_logs_repo;

    @Value("${centraldbserver.url}")
    private String centralDbServerUrl;

    @Value("${centraldbserver.clientId}")
    private String centralDbServerClientId;

    @Value("${centraldbserver.clientSecret}")
    private String centralDbServerClientSecret;

    @Value("${consentManager.delegateConsent}")
    private String delegateConsentURL;

    private String centralServerToken;

    @Autowired
    private Doctor_login_info_repo doctor_login_info_repo;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Environment environment;

    @Value("${hospital.name}")
    private String hospitalName;

    @Value("${hospital.clientSecret}")
    private String hospitalSecret;

    private PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    private Map<String,String> tokenMap=new HashMap<>();

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

    private String getCentralServerToken(){
        if(centralServerToken==null){
            RestTemplate restTemplate=new RestTemplate();
            AuthRequestDTO authRequestDTO=new AuthRequestDTO();
            authRequestDTO.setUsername(centralDbServerClientId);
            authRequestDTO.setPassword(centralDbServerClientSecret);
            String url=centralDbServerUrl + "/hospitalapp-authenticate";
            HttpHeaders httpHeaders=new HttpHeaders();
            HttpEntity<?> httpEntity=new HttpEntity<>(authRequestDTO,httpHeaders);
            ResponseEntity<String> tokenResponse=restTemplate.exchange(url, HttpMethod.POST,httpEntity,String.class);
            centralServerToken=tokenResponse.getBody();
        }
        return centralServerToken;
    }

    public String requestConsent(String doctor_id, ConsentRequestDTO consentreuestdto){

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        String token=getCentralServerToken();
        token="Bearer " + token;
        List<String> l=new ArrayList<>();
        l.add(token);
        httpHeaders.put("Authorization",l);
        HttpEntity<?> httpEntity = new HttpEntity<>(consentreuestdto,httpHeaders);
        String url=centralDbServerUrl + "/create-consent-request/" + doctor_id +"/"+ hospital_id;
        ResponseEntity<String> response=restTemplate.exchange(url,HttpMethod.POST,httpEntity,String.class);
        return response.getBody();
        /*Consent_request consent_request=new Consent_request();
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
        return true;*/
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
            String name=getPatientById(grantedConsent.getPatient_id()).getPatient_name();
            grantedConsentUIResponseDTO.setPatientName(name);
            grantedConsentUIResponseDTO.setConsent_id(grantedConsent.getConsent_id());
            grantedConsentUIResponseDTO.setDelegateAccess(grantedConsent.getDelegateAccess());
            grantedConsentUIResponseDTO.setValidity(grantedConsent.getValidity());
            grantedConsents.add(grantedConsentUIResponseDTO);
        }
        return grantedConsents;
    }

    public PatientDto getPatientById(String patientId){
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        String token=getCentralServerToken();
        token="Bearer " + token;
        List<String> l=new ArrayList<>();
        l.add(token);
        headers.put("Authorization",l);
        HttpEntity<?> httpEntity=new HttpEntity<>(headers);
        String url=centralDbServerUrl + "/get-patient/" + patientId;
        ResponseEntity<PatientDto> response=restTemplate.exchange(url,HttpMethod.GET,httpEntity,PatientDto.class);
        return response.getBody();
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
        for(DataCustodian dataCustodian:validateConsentDTO.getDataCustodians()){
            if(dataCustodian.getDataCustodianId().equals(hospital_id)){
                List<EpisodesDTO> episodesDTOs=fetchEhrFromDb(dataCustodian.getEpisodes());
                episodesDTOList.addAll(episodesDTOs);
                saveAccessLog(consent_id,patient_id,doctor_id,validateConsentDTO.getAccessPurpose());
            }
            else{
                RequestEhrDto requestEhrDto=new RequestEhrDto();
                requestEhrDto.setConsent_id(consent_id);
                requestEhrDto.setDoctor_id(doctor_id);
                requestEhrDto.setPatient_id(patient_id);
                requestEhrDto.setPurpose(validateConsentDTO.getAccessPurpose());
                requestEhrDto.setEpisodes(dataCustodian.getEpisodes());
                String token=fetchHospitalToken(dataCustodian.getDataCustodianId());
                token="Bearer " + token;
                HttpHeaders headers = new HttpHeaders();
                List<String> l=new ArrayList<>();
                l.add(token);
                headers.put("Authorization",l);
                HttpEntity<?> entity = new HttpEntity<>(requestEhrDto,headers);
                String url= environment.getProperty(dataCustodian.getDataCustodianId()+".url");
                url=url + "/get-ehr-records";
                ResponseEntity<EHRDTO> response=restTemplate.exchange(url,HttpMethod.POST,entity,EHRDTO.class);
                EHRDTO ehrResponse=response.getBody();
                episodesDTOList.addAll(ehrResponse.getEpisodesDTOList());
            }
        }
        ehrdto.setEpisodesDTOList(episodesDTOList);
        ehrdto.setEhr_id(fetchEhrIdByPatientId(patient_id));
        System.out.println(ehrdto.getEpisodesDTOList());
        return ehrdto;
    }

    private String fetchHospitalToken(String hospitalId){
        if(!tokenMap.containsKey(hospital_id)){
            String url= environment.getProperty(hospitalId+".url");
            AuthRequestDTO authRequestDTO=new AuthRequestDTO();
            authRequestDTO.setUsername(hospital_id);
            authRequestDTO.setPassword(hospitalSecret);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(authRequestDTO,httpHeaders);
            url=url + "/hospital-authenticate";
            ResponseEntity<String> response=restTemplate.exchange(url,HttpMethod.POST,httpEntity,String.class);
            String token=response.getBody();
            tokenMap.put(hospital_id,token);
        }
        return tokenMap.get(hospital_id);

    }

    public EHRDTO getEhrRecords(RequestEhrDto requestEhrDto){
        String consentToken = getToken();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        String finalToken = "Bearer " + consentToken;
        List<String> tokens = new ArrayList<>();
        tokens.add(finalToken);
        httpHeaders.put("Authorization", tokens);
        ResponseEntity<ValidateConsentDTO> validateConsent = restTemplate.exchange(validateConsentURL+"/"+requestEhrDto.getConsent_id(), HttpMethod.POST, httpEntity, ValidateConsentDTO.class);
        EHRDTO ehrdto=new EHRDTO();
        if(validateConsent.getStatusCodeValue()==200){
            List<EpisodesDTO> episodesDTOList=fetchEhrFromDb(requestEhrDto.getEpisodes());
            ehrdto.setEpisodesDTOList(episodesDTOList);
            saveAccessLog(requestEhrDto.getConsent_id(),requestEhrDto.getPatient_id(),requestEhrDto.getDoctor_id(),requestEhrDto.getPurpose());
        }
        return ehrdto;
    }

    private List<EpisodesDTO> fetchEhrFromDb(List<EpisodesDetails> episodesDetailsList){
        List<EpisodesDTO> episodesDTOList=new ArrayList<>();
        for(EpisodesDetails episodesDetails:episodesDetailsList){
            EpisodesDTO episodesDTO=new EpisodesDTO();
            episodesDTO.setEpisodeId(episodesDetails.getEpisodeId());
            episodesDTO.setEpisodeName(episodes_info_repo.getEpisodeNameById(episodesDetails.getEpisodeId()));
            List<EncountersDTO> encountersDTOList=new ArrayList<>();
            System.out.print("Encounter:"+episodesDetails.getEncounterDetails().size());
            for(EncounterDetails encounterDetails:episodesDetails.getEncounterDetails()){
                System.out.println("Encounter ID:"+encounterDetails.getEncounterId());
                EncountersDTO encountersDTO=new EncountersDTO();
                encountersDTO.setEncounterId(encounterDetails.getEncounterId());
                List<Op_Record_info> ops_recordsList=op_record_info_repo.getOpRecords(encounterDetails.getEncounterId());
                System.out.println("Db list size:"+ops_recordsList.size());
                List<Ops_recordsDTO> ops_recordsDTOList=new ArrayList<>();
                for(Op_Record_info op_record_info:ops_recordsList){
                    System.out.println("OP record ID:"+op_record_info.getOp_record_id());
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
                String doctorName=getDoctorById(doctorId).getDoctor_name();
                encountersDTO.setDoctorName(doctorName);
                encountersDTOList.add(encountersDTO);
            }
            episodesDTO.setEncounters(encountersDTOList);
            episodesDTOList.add(episodesDTO);
        }
        return episodesDTOList;
    }

    private void saveAccessLog(String consent_id,String patient_id,String doctor_id,String purpose){
        Access_logs access_logs=new Access_logs();
        access_logs.setPatient_id(patient_id);
        access_logs.setDoctor_id(doctor_id);
        access_logs.setAccess_purpose(purpose);
        access_logs.setCreated_dt(new Date());
        access_logs.setConsent_id(consent_id);
        long id=generateID();
        String access_log_id="Acc_" + id;
        access_logs.setAccess_log_id(access_log_id);
        access_logs_repo.save(access_logs);
    }

    private String fetchEhrIdByPatientId(String id){
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        String token=getCentralServerToken();
        token="Bearer " + token;
        List<String> l=new ArrayList<>();
        l.add(token);
        headers.put("Authorization",l);
        HttpEntity<?> httpEntity=new HttpEntity<>(headers);
        String url=centralDbServerUrl + "/get-ehrId/" + id;
        ResponseEntity<String> response=restTemplate.exchange(url,HttpMethod.GET,httpEntity,String.class);
        return response.getBody();
    }

    public List<EpisodesDTO> fetchEntireEhrOfPatient(String patientId){
        List<EpisodesDTO> episodes=new ArrayList<>();
        String ehr_id=fetchEhrIdByPatientId(patientId);
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
                        String doctorName=getDoctorById(encounter_info.getDoctor_id()).getDoctor_name();
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


    public Doctor_login_info getDoctorById(String doctorId){
        return doctor_login_info_repo.getLoginInfoById(doctorId);
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

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        String token=getCentralServerToken();
        token="Bearer " + token;
        List<String> l=new ArrayList<>();
        l.add(token);
        headers.put("Authorization",l);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        String url=centralDbServerUrl + "/add-doctor/" + email;
        ResponseEntity<String> response=restTemplate.exchange(url,HttpMethod.POST,httpEntity,String.class);
        return response.getBody();
        //We have to check whether the admin is adding or someone else is adding the data
        /*Doctor_info doctor=doctor_info_repo.getDoctorByEmail(email);
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
        }*/

    }



    public String registerDoctor(DoctorRegistrationDTO doctorRegistrationDto){

        String id = doctorRegistrationDto.getDoctor_id();
        String email = doctorRegistrationDto.getDoctor_email();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        String token=getCentralServerToken();
        token="Bearer " + token;
        List<String> l=new ArrayList<>();
        l.add(token);
        headers.put("Authorization",l);
        HttpEntity<?> httpEntity = new HttpEntity<>(doctorRegistrationDto,headers);
        String url=centralDbServerUrl + "/register-doctor";
        ResponseEntity<String> response=restTemplate.exchange(url,HttpMethod.POST,httpEntity,String.class);
        if(response.getStatusCodeValue()==200){
            int otp=otpService.generateOTP(doctorRegistrationDto.getDoctor_id());
            emailService.sendEmail(doctorRegistrationDto.getDoctor_email(),String.valueOf(otp),true);
        }
        return response.getBody();
        //check if the doctor with the entered id and email exist or not
        /*String ret_email = doctor_info_repo.findDoctor(id,email);
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
        }*/


    }

    public String validateOtpRegister(String doctorId,String otp){
        int storedOtp=otpService.getOtp(doctorId);
        int intOtp=Integer.parseInt(otp);
        if(storedOtp!=0){
            if(storedOtp==intOtp){
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers=new HttpHeaders();
                String token=getCentralServerToken();
                token="Bearer " + token;
                List<String> l=new ArrayList<>();
                l.add(token);
                headers.put("Authorization",l);
                HttpEntity<?> httpEntity=new HttpEntity<>(headers);
                String url=centralDbServerUrl + "/approve-doctor/" + doctorId;
                ResponseEntity<String> response=restTemplate.exchange(url,HttpMethod.POST,httpEntity,String.class);
                return "Success";
            }
            return null;
        }
        return null;
    }

    public String loginDoctor(AuthRequestDTO authRequestDTO){
        Doctor_login_info doctor_login_info= doctor_login_info_repo.getLoginInfoByEmail(authRequestDTO.getUsername());
        if(doctor_login_info!=null){
            boolean isMatch=passwordEncoder.matches(authRequestDTO.getPassword(),doctor_login_info.getDoctor_password());
            if(isMatch){
                String token=jwtService.createToken(doctor_login_info.getDoctor_id());
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

    private DoctorDto fetchDoctorInfoFromCentral(String doctorId){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        String token=getCentralServerToken();
        token="Bearer " + token;
        List<String> l=new ArrayList<>();
        l.add(token);
        headers.put("Authorization",l);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        String url=centralDbServerUrl + "/get-doctor/" + doctorId;
        ResponseEntity<DoctorDto> response=restTemplate.exchange(url,HttpMethod.GET,httpEntity,DoctorDto.class);
        return response.getBody();
    }

    public String createLogin(CreateLoginDto createLoginDto){
        DoctorDto doctorDto=fetchDoctorInfoFromCentral(createLoginDto.getDoctor_id());
        if(doctorDto!=null){
            if(doctorDto.getDoctor_name()==null){
                return null;
            }
            Doctor_login_info doctor_login_info=new Doctor_login_info();
            doctor_login_info.setDoctor_id(createLoginDto.getDoctor_id());
            doctor_login_info.setDoctor_email(createLoginDto.getUsername());
            String hash_password=passwordEncoder.encode(createLoginDto.getPassword());
            doctor_login_info.setDoctor_password(hash_password);
            doctor_login_info.setDoctor_name(doctorDto.getDoctor_name());
            doctor_login_info.setIs_verified("N");
            doctor_login_info.setIs_admin_verified("N");
            doctor_login_info_repo.save(doctor_login_info);
            sendOtp(createLoginDto.getDoctor_id());
            return "success";
        }
        return null;
    }

    public String addRecordEhr(AddEhrDto addEhrDto,String doctor_id){
        String ehrId=fetchEhrIdByPatientId(addEhrDto.getPatient_id());
        Episodes_info episode=episodes_info_repo.getEpisodeByCode(addEhrDto.getEpisode(),ehrId);
        boolean updateMappingTable=false;
        List<Encounter_info> encounter_infoList= encounter_info_repo.getEncountersByPatientId(addEhrDto.getPatient_id());
        if(encounter_infoList==null || encounter_infoList.size()==0){
            updateMappingTable=true;
        }
        if(episode==null){
            episode=new Episodes_info();
            episode.setEpisode_code(addEhrDto.getEpisode());
            episode.setCreated_dt(new Date());
            episode.setEhr_id(ehrId);
            episode.setEpisode_name(addEhrDto.getEpisode_name());
            long id=generateID();
            String episodeId="EPI_" + id;
            episode.setEpisode_id(episodeId);
            episodes_info_repo.save(episode);
        }
        String epiId=episode.getEpisode_id();
        Encounter_info encounter_info=new Encounter_info();
        encounter_info.setDoctor_id(doctor_id);
        encounter_info.setPatient_id(addEhrDto.getPatient_id());
        encounter_info.setEpisode_id(epiId);
        encounter_info.setCreated_dt(new Date());
        long id=generateID();
        String encId="ENC_" + id;
        encounter_info.setEncounter_id(encId);
        encounter_info_repo.save(encounter_info);
        Op_Record_info op_record_info=new Op_Record_info();
        op_record_info.setEncounter_id(encId);
        op_record_info.setDiagnosis(addEhrDto.getDiagnosis());
        op_record_info.setCreated_dt(new Date());
        id=generateID();
        String opId="OP_" + id;
        op_record_info.setOp_record_id(opId);
        Map<String,String> records=new HashMap<>();
        records.put("Complaints",addEhrDto.getComplaints());
        records.put("Prescription",addEhrDto.getPrescription());
        records.put("Treatment",addEhrDto.getTreatment());
        records.put("FollowUpPlan",addEhrDto.getFollowUpPlan());
        ObjectMapper objectMapper=new ObjectMapper();
        String jsonRecord=null;
        try {
            jsonRecord= objectMapper.writeValueAsString(records);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        op_record_info.setRecord_details(jsonRecord);
        op_record_info_repo.save(op_record_info);
        if(updateMappingTable){
            callMappingTableApi(addEhrDto.getPatient_id());
        }
        return "Success";
    }

    private void callMappingTableApi(String patient_id){
        String token=getCentralServerToken();
        token="Bearer " + token;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        List<String> l=new ArrayList<>();
        l.add(token);
        headers.put("Authorization",l);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        String url=centralDbServerUrl +"/update-mapping/"+patient_id+"/"+hospital_id;
        ResponseEntity<String> response=restTemplate.exchange(url,HttpMethod.POST,httpEntity,String.class);
    }

    public String sendOtp(String id){
        if(id.startsWith("PAT_")){
            PatientDto patientDto=getPatientById(id);
            if(patientDto!=null){
                Integer otp= otpService.generateOTP(id);
                emailService.sendEmail(patientDto.getPatient_email(),String.valueOf(otp),false);
                return "Success";
            }
        }
        else if(id.startsWith("DOC_")){
            Doctor_login_info doctor_login_info=getDoctorById(id);
            if(doctor_login_info!=null){
                Integer otp= otpService.generateOTP(id);
                emailService.sendEmail(doctor_login_info.getDoctor_email(),String.valueOf(otp),true);
                return "Success";
            }
        }

        return null;
    }

    public String validateOtp(String id,String otp){
        System.out.println("validate service");
        int storedOtp= otpService.getOtp(id);
        System.out.println(storedOtp);
        if(storedOtp!=0){
            int intOtp=Integer.parseInt(otp);
            if(storedOtp==intOtp){
                System.out.println("otp valid");
                if(id.startsWith("DOC_")){
                    Doctor_login_info doctor_login_info=getDoctorById(id);
                    doctor_login_info.setIs_verified("Y");
                    doctor_login_info_repo.save(doctor_login_info);
                }
                return "Success";
            }
            return null;
        }
        return null;
    }

    public List<AccessLogDto> getAccessLogs(String patientId){
        List<Access_logs> access_logs= access_logs_repo.getAccessLogsForPatient(patientId);
        List<AccessLogDto> accessLogDtoList=new ArrayList<>();
        for(Access_logs accessLog:access_logs){
            AccessLogDto accessLogDto=new AccessLogDto();
            accessLogDto.setLog_id(accessLog.getAccess_log_id());
            accessLogDto.setConsent_id(accessLog.getConsent_id());
            Doctor_login_info doctor_login_info=doctor_login_info_repo.getLoginInfoById(accessLog.getDoctor_id());
            if(doctor_login_info!=null){
                accessLogDto.setDoctor_name(doctor_login_info.getDoctor_name());
            }
            else{
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers=new HttpHeaders();
                String token=getCentralServerToken();
                token="Bearer " + token;
                List<String> l=new ArrayList<>();
                l.add(token);
                headers.put("Authorization",l);
                HttpEntity<?> httpEntity = new HttpEntity<>(headers);
                String url=centralDbServerUrl + "/get-doctor/" + accessLog.getDoctor_id();
                ResponseEntity<DoctorDto> response=restTemplate.exchange(url,HttpMethod.GET,httpEntity,DoctorDto.class);
                accessLogDto.setDoctor_name(response.getBody().getDoctor_name());
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String date=null;
            date=dateFormat.format(accessLog.getCreated_dt());
            accessLogDto.setTimestamp(date);
            accessLogDto.setHospital_name(hospitalName);
            accessLogDto.setAccess_purpose(accessLog.getAccess_purpose());
            accessLogDtoList.add(accessLogDto);
        }
        return accessLogDtoList;
    }

    public HospitalDto getHospitalById(String id){
        String token=getCentralServerToken();
        token="Bearer " + token;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        List<String> l=new ArrayList<>();
        l.add(token);
        headers.put("Authorization",l);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        String url=centralDbServerUrl + "/get-hospital/"+id;
        ResponseEntity<HospitalDto> response=restTemplate.exchange(url,HttpMethod.GET,httpEntity,HospitalDto.class);
        return response.getBody();
    }

    public String delegateconsentservice(String doctorId,String consentId,String loggedInDoctor){
        RestTemplate restTemplate=new RestTemplate();
        String delegateURL=delegateConsentURL+ "/"+doctorId+"/"+consentId;
        String consentToken=getToken();
        HttpHeaders httpHeaders=new HttpHeaders();
        HttpEntity<?> httpEntity=new HttpEntity<>(httpHeaders);
        String finalToken="Bearer " + consentToken;
        List<String> tokens=new ArrayList<>();
        tokens.add(finalToken);
        httpHeaders.put("Authorization",tokens);
        ResponseEntity<String> patientId=restTemplate.exchange(delegateURL, HttpMethod.POST, httpEntity, String.class);
        PatientDto patient=getPatientById(patientId.getBody());
        String email=patient.getPatient_email();
        DoctorDto delegatedDoctorDto=fetchDoctorInfoFromCentral(doctorId);
        DoctorDto doctorDto=fetchDoctorInfoFromCentral(loggedInDoctor);

        emailService.sendDelegateEmail(email,consentId,doctorDto.getDoctor_name(),delegatedDoctorDto.getDoctor_name());
        //String doctor=
        return "Delegation of consent successful";
    }

    public List<DoctorLoginRequestDto> getDoctorLoginRequests(){
        List<DoctorLoginRequestDto> doctorLoginRequestDtoList=new ArrayList<>();
        List<Doctor_login_info> doctor_login_infoList= doctor_login_info_repo.getDoctorLoginRequests();
        if(doctor_login_infoList!=null){
            for(Doctor_login_info doctor_login_info:doctor_login_infoList){
                DoctorLoginRequestDto doctorLoginRequestDto=new DoctorLoginRequestDto();
                doctorLoginRequestDto.setDoctor_id(doctor_login_info.getDoctor_id());
                doctorLoginRequestDto.setDoctor_name(doctor_login_info.getDoctor_name());
                doctorLoginRequestDto.setDoctor_email(doctor_login_info.getDoctor_email());
                doctorLoginRequestDtoList.add(doctorLoginRequestDto);
            }
        }
        return doctorLoginRequestDtoList;
    }

    public String acceptLoginRequest(String doctorId){
        System.out.println("Accept login service");
        Doctor_login_info doctor_login_info=doctor_login_info_repo.getLoginInfoById(doctorId);
        if(doctor_login_info!=null){
            System.out.println("Doctor found");
            doctor_login_info.setIs_admin_verified("Y");
            doctor_login_info_repo.save(doctor_login_info);
            emailService.sendLoginRequestEmail(doctor_login_info.getDoctor_email(),true);
            return "Success";
        }
        return null;
    }

    public String rejectLoginRequest(String doctorId){
        Doctor_login_info doctor_login_info=doctor_login_info_repo.getLoginInfoById(doctorId);
        if(doctor_login_info!=null){
            String email=doctor_login_info.getDoctor_email();
            doctor_login_info_repo.delete(doctor_login_info);
            emailService.sendLoginRequestEmail(email,false);
            return "Success";
        }
        return null;
    }
}
