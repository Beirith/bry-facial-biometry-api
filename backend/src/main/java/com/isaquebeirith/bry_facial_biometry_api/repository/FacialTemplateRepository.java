package com.isaquebeirith.bry_facial_biometry_api.repository;

import com.isaquebeirith.bry_facial_biometry_api.model.FacialTemplate;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacialTemplateRepository extends JpaRepository<FacialTemplate, Long> {
    Optional<FacialTemplate> findByUserId(Long userId);
}
