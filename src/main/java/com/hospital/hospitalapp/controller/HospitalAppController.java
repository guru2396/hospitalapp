package com.hospital.hospitalapp.controller;

import com.hospital.hospitalapp.DTO.ConsentRequestDTO;
import com.hospital.hospitalapp.service.HospitalAppService;
import com.hospital.hospitalapp.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HospitalAppController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private HospitalAppService hospitalAppService;

    @PostMapping(value="/request-consent")
    public ResponseEntity<?> requestConsent(@RequestBody ConsentRequestDTO consentreuestdto, @RequestHeader("Authorization") String token){
        String doctor_id=jwtService.extractID(token);
        boolean response= hospitalAppService.requestConsent(doctor_id,consentreuestdto);
        if(response)
        {
            return ResponseEntity.ok("Consent Requested successfully");
        }
        else{
            ResponseEntity<String> resp=new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return resp;
        }
    }

}
