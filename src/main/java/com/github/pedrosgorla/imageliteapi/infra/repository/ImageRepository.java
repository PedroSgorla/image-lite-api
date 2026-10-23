package com.github.pedrosgorla.imageliteapi.infra.repository;

import com.github.pedrosgorla.imageliteapi.domain.entity.Image;
import com.github.pedrosgorla.imageliteapi.domain.enums.ImageExtension;
import com.github.pedrosgorla.imageliteapi.infra.repository.specs.GenericSpecs;
import com.github.pedrosgorla.imageliteapi.infra.repository.specs.ImageSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

import static com.github.pedrosgorla.imageliteapi.infra.repository.specs.GenericSpecs.*;
import static com.github.pedrosgorla.imageliteapi.infra.repository.specs.ImageSpecs.*;
import static org.springframework.data.jpa.domain.Specification.anyOf;
import static org.springframework.data.jpa.domain.Specification.where;

public interface ImageRepository extends JpaRepository <Image, String>, JpaSpecificationExecutor<Image> {

    default List<Image> findByExtensionAndNameOrTagsLike(ImageExtension extension, String query){
        //SELECT * FROM TB_IMAGE WHERE 1 = 1
        Specification<Image> specification = where(conjuction());

        if(extension != null){
            // AND EXTENSION = 'PNG'
            specification = specification.and(extensionEqual(extension));
        }

        if(StringUtils.hasText(query)){
            //AND ( NAME LIKE 'QUERY' OR TAGS LIKE 'QUERY' )
            specification = specification.and(anyOf(nameLike(query), tagsLike(query)));
        }

        return findAll(specification);
    }
}
