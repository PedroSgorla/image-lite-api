package com.github.pedrosgorla.imageliteapi.jwt;

import com.github.pedrosgorla.imageliteapi.domain.AccessToken;
import com.github.pedrosgorla.imageliteapi.domain.entity.User;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public AccessToken generateToken(User user){
        return new AccessToken("");
    }
}
