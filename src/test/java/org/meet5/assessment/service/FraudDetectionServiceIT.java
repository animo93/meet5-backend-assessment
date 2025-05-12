package org.meet5.assessment.service;

import org.junit.jupiter.api.Test;
import org.meet5.assessment.MainApplication;
import org.meet5.assessment.repository.LikeRepository;
import org.meet5.assessment.repository.UserRepository;
import org.meet5.assessment.repository.VisitRepository;
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

@SpringBootTest(classes = MainApplication.class)
@Testcontainers
@TestPropertySource(properties = {
        "fraud.user.config.created_within=10",
        "fraud.user.config.allowed_limit=5"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/scripts/001_ddl_create_user.sql","/scripts/002_ddl_create_visit.sql","/scripts/003_ddl_create_like.sql"})
public class FraudDetectionServiceIT {

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

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Test
    public void testContextLoad(){}

    @Test
    public void detectFraudulentUsers_forValidUsers_shouldUpdateUserStatusToValid() {
        // Given
        var user1 = userRepository.createUser("john", 30, "NEW");
        var user2 = userRepository.createUser("doe", 30, "NEW");
        visitRepository.createVisit(user1, user2);
        visitRepository.createVisit(user2, user1);

        // When
        fraudDetectionService.detectFraudulentUsers();

        // Then
        var user1Status = userRepository.getUserById(user1).get().getStatus();
        var user2Status = userRepository.getUserById(user2).get().getStatus();

        assert(user1Status.equals("VALID"));
        assert(user2Status.equals("VALID"));
    }

    @Test
    public void detectFraudulentUsers_forFraudulentUsers_shouldUpdateUserStatusToFraud() {
        // Given
        var user1 = userRepository.createUser("user1", 30, "NEW");
        var user2 = userRepository.createUser("user2", 30, "NEW");
        var user3 = userRepository.createUser("user3", 30, "NEW");
        var user4 = userRepository.createUser("user4", 30, "NEW");
        var user5 = userRepository.createUser("user5", 30, "NEW");

        // Fraudulent user has visited and liked all other users
        var fraudulentUser = userRepository.createUser("fraudulentUser", 30, "NEW");
        visitRepository.createVisit(fraudulentUser, user1);
        likeRepository.createLike(fraudulentUser,user1);
        visitRepository.createVisit(fraudulentUser, user2);
        likeRepository.createLike(fraudulentUser,user2);
        visitRepository.createVisit(fraudulentUser, user3);
        likeRepository.createLike(fraudulentUser,user3);
        visitRepository.createVisit(fraudulentUser, user4);
        likeRepository.createLike(fraudulentUser,user4);
        visitRepository.createVisit(fraudulentUser, user5);
        likeRepository.createLike(fraudulentUser,user5);

        // When
        fraudDetectionService.detectFraudulentUsers();

        // Then
        assert(userRepository.getUserById(user1).get().getStatus().equals("VALID"));
        assert(userRepository.getUserById(user2).get().getStatus().equals("VALID"));
        assert(userRepository.getUserById(user3).get().getStatus().equals("VALID"));
        assert(userRepository.getUserById(user4).get().getStatus().equals("VALID"));
        assert(userRepository.getUserById(user5).get().getStatus().equals("VALID"));
        assert(userRepository.getUserById(fraudulentUser).get().getStatus().equals("FRAUD"));
    }
}
