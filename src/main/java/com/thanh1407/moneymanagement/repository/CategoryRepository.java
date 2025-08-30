package com.thanh1407.moneymanagement.repository;

import com.thanh1407.moneymanagement.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByProfileId(Long profileId);

    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    List<CategoryEntity> findByProfileIdAndType(Long profileId, String type);

    Boolean existsByNameAndProfileId(String name, Long profileId);
}
