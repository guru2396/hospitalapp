package com.hospital.hospitalapp.controller;

import com.hospital.hospitalapp.DTO.*;
import com.hospital.hospitalapp.central.entity.PatientInfo;
import com.hospital.hospitalapp.service.HospitalAppService;
import com.hospital.hospitalapp.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HospitalAppController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private HospitalAppService hospitalAppService;

    @Value("${patientapp.secret}")
    private String patientAppSecret;

    @PostMapping(value="/request-consent")
    public ResponseEntity<?> requestConsent(@RequestBody ConsentRequestDTO consentreuestdto, @RequestHeader("Authorization") String token){
       // String doctor_id=jwtService.extractID(token);
        boolean response= hospitalAppService.requestConsent(token,consentreuestdto);
        if(response)
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
        String doctor_id=token;//jwtService.extractID(token);
        List<GrantedConsentUIResponseDTO> grantedConsents=hospitalAppService.getGrantedConsents(doctor_id);
        ResponseEntity<List<GrantedConsentUIResponseDTO>> resp=new ResponseEntity<>(grantedConsents, HttpStatus.OK);
        return resp;
    }

    @GetMapping(value = "/get-ehr/{patient_id}/{consent_id}")
    public ResponseEntity<?> getEHR(@RequestHeader("Authorization")String token,@PathVariable("patient_id") String patient_id,@PathVariable("consent_id") String consent_id){
        String doctor_id=token;//jwtService.extractID(token);
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
        PatientInfo patient= hospitalAppService.getPatientById(authRequestDTO.getUsername());
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
        else{
            ResponseEntity<String> response=new ResponseEntity<>("Unauthorized",HttpStatus.UNAUTHORIZED);
            return response;
        }
    }
}
