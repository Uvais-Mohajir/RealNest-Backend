package com.realnest.repository;

import com.realnest.entity.User;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail_and_existsByEmail_shouldWork() {
        User user = User.builder()
                .name("Alice")
                .email("alice@realnest.com")
                .password("secret")
                .role(Role.CUSTOMER)
                .build();
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("alice@realnest.com");
        boolean exists = userRepository.existsByEmail("alice@realnest.com");

        assertTrue(found.isPresent());
        assertTrue(exists);
    }
}
