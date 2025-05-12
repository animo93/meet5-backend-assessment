package org.meet5.assessment.repository;

import lombok.AllArgsConstructor;
import org.meet5.assessment.dao.UserDAO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final String CREATE_USER_SQL = "INSERT INTO users (name, age, created_at, status) VALUES (:name, :age, :createdAt, :status)";
    private static final String GET_USER_SQL = "SELECT * FROM users WHERE user_id = :id";
    private static final String GET_ALL_USERS_WITH_STATUS_SQL = "SELECT * FROM users WHERE status = :status";
    private static final String UPDATE_USER_STATUS_SQL = "UPDATE users SET status = :status WHERE user_id = :userId";

    @Transactional(propagation = Propagation.REQUIRED)
    public long createUser(String name, int age, String status) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(CREATE_USER_SQL,
                new MapSqlParameterSource(Map.of("name", name, "age", age, "createdAt", LocalDateTime.now(), "status", status)),
                keyHolder,
                new String[]{"user_id"});
        return keyHolder.getKey().longValue();
    }

    public Optional<UserDAO> getUserById(long id) {
        return jdbcTemplate.query(GET_USER_SQL,
                Map.of("id", id),
                (rs, rowNum) -> {
                    var userDAO = new UserDAO();
                    userDAO.setUserId(rs.getLong("user_id"));
                    userDAO.setName(rs.getString("name"));
                    userDAO.setAge(rs.getInt("age"));
                    userDAO.setStatus(rs.getString("status"));
                    return userDAO;
                }
        ).stream().findFirst();
    }

    public List<Long> getUserIDsByStatus(String status) {
        return jdbcTemplate.query(GET_ALL_USERS_WITH_STATUS_SQL, Map.of("status", status), (rs, rowNum) -> rs.getLong("user_id"));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserStatus(Long userId, String status){
        jdbcTemplate.update(UPDATE_USER_STATUS_SQL, Map.of("status", status, "userId", userId));
    }
}
