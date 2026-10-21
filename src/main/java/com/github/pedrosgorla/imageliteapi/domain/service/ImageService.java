package com.github.pedrosgorla.imageliteapi.domain.service;

import com.github.pedrosgorla.imageliteapi.domain.entity.Image;

import java.util.Optional;

public interface ImageService {

    Image save(Image image);

    Optional<Image> getById(String id);
}
