package com.hospital.hospitalapp.controller;

import com.hospital.hospitalapp.DTO.*;
import com.hospital.hospitalapp.service.HospitalAppService;
import com.hospital.hospitalapp.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"*"})
public class HospitalAppController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private HospitalAppService hospitalAppService;

    @Value("${patientapp.secret}")
    private String patientAppSecret;

    @PostMapping(value="/request-consent")
    public ResponseEntity<?> requestConsent(@RequestBody ConsentRequestDTO consentreuestdto, @RequestHeader("Authorization") String token){
        String doctor_id=jwtService.extractID(token);
        String response= hospitalAppService.requestConsent(doctor_id,consentreuestdto);
        if(response!=null)
        {
            return ResponseEntity.ok("Consent Requested successfully");
        }
        else{
            ResponseEntity<String> resp=new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return resp;
        }
    }

    @GetMapping(value = "/get-granted-consents")
    public ResponseEntity<?> getGrantedConsents(@RequestHeader("Authorization") String token){
        String doctor_id=jwtService.extractID(token);
        List<GrantedConsentUIResponseDTO> grantedConsents=hospitalAppService.getGrantedConsents(doctor_id);
        ResponseEntity<List<GrantedConsentUIResponseDTO>> resp=new ResponseEntity<>(grantedConsents, HttpStatus.OK);
        return resp;
    }

    @GetMapping(value = "/get-ehr/{patient_id}/{consent_id}")
    public ResponseEntity<?> getEHR(@RequestHeader("Authorization")String token,@PathVariable("patient_id") String patient_id,@PathVariable("consent_id") String consent_id){
        String doctor_id=jwtService.extractID(token);
        EHRDTO response= hospitalAppService.getEHR(consent_id,patient_id,doctor_id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/get-ehr-patient/{patient_id}")
    public ResponseEntity<?> getEntireEhrPatient(@PathVariable("patient_id") String patient_id){
        List<EpisodesDTO> episodes= hospitalAppService.fetchEntireEhrOfPatient(patient_id);
        return ResponseEntity.ok(episodes);
    }

    @PostMapping(value="/patient-authenticate")
    public ResponseEntity<?> authenticatePatientApp(@RequestBody AuthRequestDTO authRequestDTO){
        PatientDto patient= hospitalAppService.getPatientById(authRequestDTO.getUsername());
        if(patient!=null){
            if(patientAppSecret.equals(authRequestDTO.getPassword())){
                String token=jwtService.createToken(patient.getPatient_id());
                return ResponseEntity.ok(token);
            }
            else{
                ResponseEntity<String> response=new ResponseEntity<>("Unauthorized",HttpStatus.UNAUTHORIZED);
                return response;
            }
        }
        else {
            ResponseEntity<String> response = new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            return response;
        }
    }

    @PostMapping(value="/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody AdminLoginDTO adminLoginDto){
        String token= hospitalAppService.loginAdmin(adminLoginDto);
        if(token==null){
            ResponseEntity<String> response=new ResponseEntity<>("Unauthorized",HttpStatus.UNAUTHORIZED);
            return response;
        }
        return ResponseEntity.ok(token);
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping(value="/add-doctor")
    public ResponseEntity<?> addDoctor(@RequestBody DoctorRegistrationDTO doctorRegistrationDto){
        String id = hospitalAppService.addDoctor(doctorRegistrationDto);
        System.out.println(id);
        if(id==null){
            ResponseEntity<String> response=new ResponseEntity<>("Doctor with this email already exists ",HttpStatus.INTERNAL_SERVER_ERROR);
            return response;
        }
        return ResponseEntity.ok(id);
    }

    @PostMapping(value="/register-doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorRegistrationDTO doctorRegistrationDto){
        String msg= hospitalAppService.registerDoctor(doctorRegistrationDto);
        if(msg==null){
            ResponseEntity<String> response=new ResponseEntity<>("Doctor does not exist in the system ",HttpStatus.INTERNAL_SERVER_ERROR);
            return response;
        }
        return ResponseEntity.ok(msg);
    }

    @PostMapping(value = "/login-doctor")
    public ResponseEntity<?> loginDoctor(@RequestBody AuthRequestDTO authRequestDTO){
        String token= hospitalAppService.loginDoctor(authRequestDTO);
        if(token==null){
            ResponseEntity<String> response=new ResponseEntity<>("Unauthorized",HttpStatus.UNAUTHORIZED);
            return response;
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/create-login")
    public ResponseEntity<?> createLogin(@RequestBody CreateLoginDto createLoginDto){
        String msg= hospitalAppService.createLogin(createLoginDto);
        if(msg==null){
            ResponseEntity<String> response=new ResponseEntity<>("Doctor not found",HttpStatus.NOT_FOUND);
            return response;
        }
        return ResponseEntity.ok(msg);
    }

    @PostMapping(value = "/add-record-ehr")
    public ResponseEntity<?> addEhrRecord(@RequestBody AddEhrDto addEhrDto,@RequestHeader("Authorization") String token){
        String doctor_id=jwtService.extractID(token);
        String msg= hospitalAppService.addRecordEhr(addEhrDto,doctor_id);
        return ResponseEntity.ok(msg);
    }

    @PostMapping(value="/send-otp/{id}")
    public ResponseEntity<?> sendOtpToPatient(@PathVariable("id") String id){
        String status= hospitalAppService.sendOtp(id);
        return ResponseEntity.ok(status);
    }

    @PostMapping(value="/validate-otp/{patientId}/{otp}")
    public ResponseEntity<?> validateOtp(@PathVariable("patientId") String patientId,@PathVariable("otp") String otp){
        System.out.println("validate controller");
        String status= hospitalAppService.validateOtp(patientId,otp);
        if(status==null){
            ResponseEntity<String> response=new ResponseEntity<>("Wrong otp",HttpStatus.UNAUTHORIZED);
            return response;
        }
        return ResponseEntity.ok(status);
    }


    @GetMapping(value="/get-access-logs/{patientId}")
    public ResponseEntity<?> getAccessLogs(@PathVariable("patientId") String patientId){
        List<AccessLogDto> accessLogDtoList= hospitalAppService.getAccessLogs(patientId);
        return ResponseEntity.ok(accessLogDtoList);
    }

    @PostMapping(value="/delegate-consent/{doctorId}/{consentId}")
    public ResponseEntity<?> delegateConsent(@PathVariable("doctorId") String doctorId,@PathVariable("consentId") String consentId,@RequestHeader("Authorization") String token){
        //System.out.println("validate controller");
        String loggedInDoctor= jwtService.extractID(token);
        String status= hospitalAppService.delegateconsentservice(doctorId,consentId,loggedInDoctor);
        if(status==null){
            ResponseEntity<String> response=new ResponseEntity<>("error in delegation of consent",HttpStatus.UNAUTHORIZED);
            return response;
        }
        return ResponseEntity.ok(status);
    }

}
