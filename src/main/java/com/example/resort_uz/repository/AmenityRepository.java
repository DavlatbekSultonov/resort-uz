package com.example.resort_uz.repository;

import com.example.resort_uz.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    boolean existsByName(String name);
    Optional<Amenity> findByName(String name);
    List<Amenity> findByCategory(Amenity.AmenityCategory category);
}
