package org.meet5.assessment.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final String CREATE_USER_SQL = "INSERT INTO users (name, age, created_at, status) VALUES (:name, :age, :createdAt, :status)";
    private static final String GET_USER_SQL = "SELECT * FROM users WHERE id = :id";
    private static final String GET_ALL_USERS_WITH_STATUS_SQL = "SELECT * FROM users WHERE status = :status";
    private static final String UPDATE_USER_STATUS_SQL = "UPDATE users SET status = :status WHERE user_id = :userId";

    public void createUser(String name, int age, String status) {
        jdbcTemplate.update(CREATE_USER_SQL, Map.of("name", name, "age", age, "createdAt", LocalDateTime.now(), "status", status));
    }

    public Map<String, Object> getUserById(int id) {
        return jdbcTemplate.queryForMap(GET_USER_SQL, Map.of("id", id));
    }

    public List<Long> getUserIDsByStatus(String status) {
        return jdbcTemplate.query(GET_ALL_USERS_WITH_STATUS_SQL, Map.of("status", status), (rs, rowNum) -> rs.getLong("user_id"));
    }

    public void updateUserStatus(Long userId, String status){
        jdbcTemplate.update(UPDATE_USER_STATUS_SQL, Map.of("status", status, "userId", userId));
    }
}
