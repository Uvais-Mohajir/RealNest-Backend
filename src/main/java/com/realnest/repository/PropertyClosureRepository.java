package com.realnest.repository;

import com.realnest.entity.PropertyClosure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PropertyClosureRepository extends JpaRepository<PropertyClosure, Long> {
    Optional<PropertyClosure> findByPropertyId(Long propertyId);
}
