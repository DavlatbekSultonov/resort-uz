package com.example.resort_uz.repository;

import com.example.resort_uz.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByResortIdOrderBySortOrderAsc(Long resortId);
    Optional<Photo> findByResortIdAndIsCoverTrue(Long resortId);
    int countByResortId(Long resortId);

    @Modifying
    @Query("UPDATE Photo p SET p.isCover = false WHERE p.resort.id = :resortId")
    void removeCoverByResortId(@Param("resortId") Long resortId);
}
