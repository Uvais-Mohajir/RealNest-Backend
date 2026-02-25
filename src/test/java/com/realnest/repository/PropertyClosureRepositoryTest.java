package com.realnest.repository;

import com.realnest.entity.Property;
import com.realnest.entity.PropertyClosure;
import com.realnest.entity.User;
import com.realnest.enums.PropertyStatus;
import com.realnest.enums.PropertyType;
import com.realnest.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
class PropertyClosureRepositoryTest {

    @Autowired
    private PropertyClosureRepository propertyClosureRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByPropertyId_shouldReturnClosure() {
        User owner = User.builder().name("Owner").email("owner3@realnest.com").password("p").role(Role.OWNER).build();
        User customer = User.builder().name("Customer").email("c@realnest.com").password("p").role(Role.CUSTOMER).build();
        entityManager.persist(owner);
        entityManager.persist(customer);

        Property property = Property.builder()
                .title("2BHK")
                .description("desc")
                .price(25000.0)
                .type(PropertyType.RENT)
                .city("Mumbai")
                .location("Andheri")
                .imageUrl("img")
                .status(PropertyStatus.APPROVED)
                .owner(owner)
                .build();
        entityManager.persist(property);

        PropertyClosure closure = PropertyClosure.builder()
                .property(property)
                .user(customer)
                .price(25000.0)
                .location("Andheri")
                .closureType(PropertyStatus.SOLD)
                .build();
        entityManager.persistAndFlush(closure);

        Optional<PropertyClosure> found = propertyClosureRepository.findByPropertyId(property.getId());
        assertTrue(found.isPresent());
    }
}
