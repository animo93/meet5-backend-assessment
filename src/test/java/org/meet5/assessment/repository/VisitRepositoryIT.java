package org.meet5.assessment.repository;

import org.junit.jupiter.api.Test;
import org.meet5.assessment.MainApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest(classes = MainApplication.class)
@Testcontainers
@TestPropertySource(properties = {
        "fraud.user.config.created_within=10",
        "fraud.user.config.allowed_limit=5"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"/scripts/001_ddl_create_user.sql","/scripts/002_ddl_create_visit.sql","/scripts/003_ddl_create_like.sql"})
public class VisitRepositoryIT {

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
    private VisitRepository visitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Test
    public void testContextLoad(){}

    @Test
    @Transactional
    public void createVisit_whenCreated_shouldReturnTrueWhenChecked(){

        // When
        var visitor = userRepository.createUser("john", 30, "NEW");
        var visited = userRepository.createUser("doe", 30, "NEW");
        visitRepository.createVisit(visitor, visited);

        // Then
        var visitExists = visitRepository.checkVisitExists(visitor, visited);
        assert(visitExists);
    }

    @Test
    @Transactional
    public void getAllVisitors_whenCreated_shouldReturnListOfVisitors(){

        // When
        var visitor = userRepository.createUser("john", 30, "NEW");
        var visited = userRepository.createUser("doe", 30, "NEW");
        visitRepository.createVisit(visitor, visited);

        // Then
        var visitors = visitRepository.getAllVisitors(visited);
        assert(visitors.isPresent());
        assert(visitors.get().size() == 1);
    }

    @Test
    @Transactional
    public void getFraudulentUsers_whenCreated_shouldReturnOneFraudulentUsers(){

        // When
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

        // Then
        var fraudulentUsers = visitRepository.getFraudulentUsers(List.of(user1,user2,user3,user4,fraudulentUser),
                10, 2);
        assert(fraudulentUsers.size() == 1);
        assert(fraudulentUsers.getFirst() == fraudulentUser);
    }

    @Test
    @Transactional
    public void getFraudulentUsers_whenCreatedAndOnlyVisitedButNotLiked_shouldReturnZeroFraudulentUsers(){

        // When
        var user1 = userRepository.createUser("user1", 30, "NEW");
        var user2 = userRepository.createUser("user2", 30, "NEW");
        var user3 = userRepository.createUser("user3", 30, "NEW");
        var user4 = userRepository.createUser("user4", 30, "NEW");
        var user5 = userRepository.createUser("user5", 30, "NEW");

        // Fraudulent user has only visited but not liked all other users
        var fraudulentUser = userRepository.createUser("fraudulentUser", 30, "NEW");
        visitRepository.createVisit(fraudulentUser, user1);
        visitRepository.createVisit(fraudulentUser, user2);
        visitRepository.createVisit(fraudulentUser, user3);
        visitRepository.createVisit(fraudulentUser, user4);
        visitRepository.createVisit(fraudulentUser, user5);

        // Then
        var fraudulentUsers = visitRepository.getFraudulentUsers(List.of(user1,user2,user3,user4,fraudulentUser),
                10, 2);
        assert(fraudulentUsers.isEmpty());
    }

    @Test
    @Transactional
    public void getFraudulentUsers_whenCreatedAndOnlyLikedButNotVisited_shouldReturnZeroFraudulentUsers(){

        // When
        var user1 = userRepository.createUser("user1", 30, "NEW");
        var user2 = userRepository.createUser("user2", 30, "NEW");
        var user3 = userRepository.createUser("user3", 30, "NEW");
        var user4 = userRepository.createUser("user4", 30, "NEW");
        var user5 = userRepository.createUser("user5", 30, "NEW");

        // Fraudulent user has only visited but not liked all other users
        var fraudulentUser = userRepository.createUser("fraudulentUser", 30, "NEW");
        likeRepository.createLike(fraudulentUser,user1);
        likeRepository.createLike(fraudulentUser,user2);
        likeRepository.createLike(fraudulentUser,user3);
        likeRepository.createLike(fraudulentUser,user4);
        likeRepository.createLike(fraudulentUser,user5);

        // Then
        var fraudulentUsers = visitRepository.getFraudulentUsers(List.of(user1,user2,user3,user4,fraudulentUser),
                10, 2);
        assert(fraudulentUsers.isEmpty());
    }

}
