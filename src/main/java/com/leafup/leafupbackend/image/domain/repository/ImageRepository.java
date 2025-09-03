package com.leafup.leafupbackend.image.domain.repository;

import com.leafup.leafupbackend.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
