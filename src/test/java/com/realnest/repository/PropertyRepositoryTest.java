package com.realnest.repository;

import com.realnest.entity.Property;
import com.realnest.entity.User;
import com.realnest.enums.PropertyStatus;
import com.realnest.enums.PropertyType;
import com.realnest.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.sql.init.mode=always"
})
class PropertyRepositoryTest {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByOwnerId_and_findByStatus_shouldWork() {
        User owner = User.builder()
                .name("Owner")
                .email("owner@realnest.com")
                .password("secret")
                .role(Role.OWNER)
                .build();
        entityManager.persist(owner);

        Property property = Property.builder()
                .title("2BHK")
                .description("Near metro")
                .price(25000.0)
                .type(PropertyType.RENT)
                .city("Mumbai")
                .location("Andheri")
                .imageUrl("img")
                .status(PropertyStatus.PENDING)
                .owner(owner)
                .build();
        entityManager.persistAndFlush(property);

        List<Property> byOwner = propertyRepository.findByOwnerId(owner.getId());
        List<Property> byStatus = propertyRepository.findByStatus(PropertyStatus.PENDING);

        assertEquals(1, byOwner.size());
        assertEquals(1, byStatus.size());
    }

    @Test
    void searchApproved_shouldFilterCorrectly() {
        User owner = User.builder()
                .name("Owner")
                .email("owner2@realnest.com")
                .password("secret")
                .role(Role.OWNER)
                .build();
        entityManager.persist(owner);

        Property approved = Property.builder()
                .title("Luxury Apartment")
                .description("Gated community")
                .price(50000.0)
                .type(PropertyType.RENT)
                .city("Mumbai")
                .location("Bandra")
                .imageUrl("img")
                .status(PropertyStatus.APPROVED)
                .owner(owner)
                .build();
        Property pending = Property.builder()
                .title("Pending Flat")
                .description("Pending")
                .price(15000.0)
                .type(PropertyType.RENT)
                .city("Mumbai")
                .location("Kurla")
                .imageUrl("img")
                .status(PropertyStatus.PENDING)
                .owner(owner)
                .build();
        entityManager.persist(approved);
        entityManager.persistAndFlush(pending);

        List<Property> result = propertyRepository.searchApproved("Mumbai", "Luxury", PropertyType.RENT);

        assertEquals(1, result.size());
        assertEquals("Luxury Apartment", result.getFirst().getTitle());
    }
}
