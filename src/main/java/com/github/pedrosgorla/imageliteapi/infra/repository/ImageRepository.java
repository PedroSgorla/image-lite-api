package com.github.pedrosgorla.imageliteapi.infra.repository;

import com.github.pedrosgorla.imageliteapi.domain.entity.Image;
import com.github.pedrosgorla.imageliteapi.domain.enums.ImageExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

public interface ImageRepository extends JpaRepository <Image, String>, JpaSpecificationExecutor<Image> {

    default List<Image> findByExtensionAndNameOrTagsLike(ImageExtension extension, String query){
        //SELECT * FROM TB_IMAGE WHERE 1 = 1
        Specification<Image> conjunction = (root, q, criteriaBuilder) -> criteriaBuilder.conjunction();
        Specification<Image> specification = Specification.where(conjunction);

        if(extension != null){
            // AND EXTENSION = 'PNG'
            Specification<Image> extensionEqual = ((root, q, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("extension"), extension));
            specification = specification.and(extensionEqual);
        }

        if(StringUtils.hasText(query)){
            //AND ( NAME LIKE 'QUERY' OR TAGS LIKE 'QUERY' )
            Specification<Image> nameLike = (root, q, criteriaBuilder) -> criteriaBuilder
                    .like(criteriaBuilder.upper(root.get("name")), "%" + query.toUpperCase() + "%");
            Specification<Image> tagsLike = (root, q, criteriaBuilder) -> criteriaBuilder
                    .like(criteriaBuilder.upper(root.get("tags")), "%" + query.toUpperCase() + "%");

            Specification<Image> nameOrTagsLike = Specification.anyOf(nameLike, tagsLike);

            specification = specification.and(nameOrTagsLike);
        }

        return findAll(specification);
    }
}
