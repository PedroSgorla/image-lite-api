package com.github.pedrosgorla.imageliteapi.jwt;

import com.github.pedrosgorla.imageliteapi.domain.AccessToken;
import com.github.pedrosgorla.imageliteapi.domain.entity.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecretKeyGenerator secretKeyGenerator;

    public AccessToken generateToken(User user){

        var key = secretKeyGenerator.getKey();
        var expirationDate = generateExpirationsDate();
        var claims = generateTokenClaims(user);

        String token = Jwts
                .builder()
                .signWith(key)
                .subject(user.getEmail())
                .expiration(expirationDate)
                .claims(claims)
                .compact();

        return new AccessToken(token);
    }

    private Date generateExpirationsDate(){
        var expirationMinutes = 60;
        LocalDateTime now = LocalDateTime.now().plusMinutes(60);
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Map<String, Object> generateTokenClaims(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        return claims;
    }

    public String getEmailFromToken(String tokenJwt){
        try{
            return Jwts.parser()
                    .verifyWith(secretKeyGenerator.getKey())
                    .build()
                    .parseSignedClaims(tokenJwt)
                    .getPayload()
                    .getSubject();
        }catch (JwtException e){
            throw new InvalidTokenException(e.getMessage());
        }

    }

}
