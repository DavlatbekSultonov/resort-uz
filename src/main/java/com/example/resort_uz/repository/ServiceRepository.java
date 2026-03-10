package com.example.resort_uz.repository;

import com.example.resort_uz.entity.Service_entity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service_entity, Long> {


    List<Service> findByResortIdAndActiveTrue(Long resortId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Service_entity s WHERE s.resort.id = :resortId")
    void deleteByResortId(@Param("resortId") Long resortId);
}
