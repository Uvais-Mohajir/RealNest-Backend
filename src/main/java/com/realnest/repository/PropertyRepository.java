package com.realnest.repository;

import com.realnest.entity.Property;
import com.realnest.enums.PropertyStatus;
import com.realnest.enums.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwnerId(Long ownerId);
    List<Property> findByStatus(PropertyStatus status);

    @Query("""
            select p from Property p
            where p.status = com.realnest.enums.PropertyStatus.APPROVED
            and (:city is null or lower(p.city) like lower(concat('%', :city, '%')))
            and (:keyword is null or lower(p.title) like lower(concat('%', :keyword, '%')) or lower(p.description) like lower(concat('%', :keyword, '%')))
            and (:type is null or p.type = :type)
            """)
    List<Property> searchApproved(
            @Param("city") String city,
            @Param("keyword") String keyword,
            @Param("type") PropertyType type
    );
}
