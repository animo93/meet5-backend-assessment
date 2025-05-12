package org.meet5.assessment.repository;

import org.junit.jupiter.api.Test;
import org.meet5.assessment.MainApplication;
import org.meet5.assessment.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = MainApplication.class)
@Testcontainers
@TestPropertySource(properties = {
        "fraud.user.config.created_within=10",
        "fraud.user.config.allowed_limit=5"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/scripts/001_ddl_create_user.sql","/scripts/002_ddl_create_visit.sql","/scripts/003_ddl_create_like.sql"})
public class UserRepositoryIT {

    @Container
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void configurePostgreSQL(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testContextLoad(){}

    @Test
    public void createUser_whenValidUser_shouldReturnUser() {
        // Given
        var userDAO = new UserDAO();
        userDAO.setName("john");
        userDAO.setAge(30);

        // When
        var id = userRepository.createUser(userDAO.getName(), userDAO.getAge(), "NEW");

        // Then
        var user = userRepository.getUserById(id);
        assertNotNull(user);
        assert(user.isPresent());
        assert(user.get().getUserId() == id);
        assert(user.get().getName().equals(userDAO.getName()));
        assert(user.get().getAge() == userDAO.getAge());
        assert(user.get().getStatus().equals("NEW"));
    }
}
