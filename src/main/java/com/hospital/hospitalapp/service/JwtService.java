package com.hospital.hospitalapp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtService {

    public String createToken(String subject){
        Map<String,Object> claims=new HashMap<>();
        String token= Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*10))
                .signWith(SignatureAlgorithm.HS256,"secret").compact();
        return token;
    }

    public String extractID(String token){
        token=token.substring(7);
        Claims claims=Jwts.parser().setSigningKey("secret").parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}
