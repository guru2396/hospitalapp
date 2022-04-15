package com.hospital.hospitalapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Properties;

@Service
public class EmailService {

    private JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();

    @Value("${hospital.emailId}")
    private String myEmail;

    @Value("${hospital.password}")
    private String myPassword;


    public void sendEmail(String emailId,String otp){
        javaMailSender.setPort(587);
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setUsername(myEmail);
        javaMailSender.setPassword(myPassword);
        Properties properties=javaMailSender.getJavaMailProperties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setTo(emailId);
        mailMessage.setSubject("OTP for consent verification");
        String text="\nOTP: "+otp+"\nPlease provide this otp to the doctor if you want to give consent to add record";
        mailMessage.setText(text);
        mailMessage.setFrom(myEmail);
        javaMailSender.send(mailMessage);
    }

}
