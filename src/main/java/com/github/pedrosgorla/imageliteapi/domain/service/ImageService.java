package com.github.pedrosgorla.imageliteapi.domain.service;

import com.github.pedrosgorla.imageliteapi.domain.entity.Image;
import com.github.pedrosgorla.imageliteapi.domain.enums.ImageExtension;

import java.util.List;
import java.util.Optional;

public interface ImageService {

    Image save(Image image);

    Optional<Image> getById(String id);

    List<Image> search (ImageExtension extension, String query);
}
