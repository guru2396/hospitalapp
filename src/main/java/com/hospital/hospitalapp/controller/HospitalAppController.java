package com.hospital.hospitalapp.controller;

import com.hospital.hospitalapp.DTO.ConsentRequestDTO;
import com.hospital.hospitalapp.DTO.EHRDTO;
import com.hospital.hospitalapp.DTO.GrantedConsentUIResponseDTO;
import com.hospital.hospitalapp.service.HospitalAppService;
import com.hospital.hospitalapp.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
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

}
