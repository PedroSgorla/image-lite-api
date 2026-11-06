package com.github.pedrosgorla.imageliteapi.domain.service;

import com.github.pedrosgorla.imageliteapi.domain.AccessToken;
import com.github.pedrosgorla.imageliteapi.domain.entity.User;

public interface UserService {

    User getByEmail(String email);
    User save(User user);
    AccessToken authenticate(String email, String password);

}
