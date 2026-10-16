package com.github.pedrosgorla.imageliteapi.infra.repository;

import com.github.pedrosgorla.imageliteapi.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository <Image, String>{
}
