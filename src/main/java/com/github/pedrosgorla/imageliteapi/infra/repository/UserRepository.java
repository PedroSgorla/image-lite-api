package com.github.pedrosgorla.imageliteapi.infra.repository;

import com.github.pedrosgorla.imageliteapi.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
