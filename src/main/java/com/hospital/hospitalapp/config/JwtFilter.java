package com.hospital.hospitalapp.config;

import com.hospital.hospitalapp.central.entity.Doctor_info;
import com.hospital.hospitalapp.central.entity.PatientInfo;
import com.hospital.hospitalapp.service.HospitalAppService;
import com.hospital.hospitalapp.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private HospitalAppService hospitalAppService;

    @Value("${admin.username}")
    private String adminUsername;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("called");
        String auth=request.getHeader("Authorization");
        System.out.println(auth);
        if(auth!=null && !"".equals(auth) && auth.startsWith("Bearer ")){
            String id=jwtService.extractID(auth);
            System.out.println(id);
            if(id!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                if(id.startsWith("PAT_") && request.getRequestURI().contains("/get-ehr-patient")){
                    PatientInfo patient= hospitalAppService.getPatientById(id);
                    if(patient!=null){
                        UsernamePasswordAuthenticationToken ut=new UsernamePasswordAuthenticationToken(patient,null,null);
                        ut.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(ut);
                    }
                }
                else if(id.startsWith("DOC_") && !request.getRequestURI().contains("/get-ehr-patient")){
                    Doctor_info doctor_info= hospitalAppService.getDoctorById(id);
                    if(doctor_info!=null){
                        UsernamePasswordAuthenticationToken ut=new UsernamePasswordAuthenticationToken(doctor_info,null,null);
                        ut.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(ut);
                    }
                }
                else if(id.equals(adminUsername)){
                    System.out.println(id);
                    UsernamePasswordAuthenticationToken ut=new UsernamePasswordAuthenticationToken(adminUsername,null,null);
                    ut.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(ut);
                }

            }
        }
        filterChain.doFilter(request,response);
    }
}
