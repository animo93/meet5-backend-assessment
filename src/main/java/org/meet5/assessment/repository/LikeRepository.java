package org.meet5.assessment.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;

@Repository
@AllArgsConstructor
public class LikeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final String CREATE_LIKE_SQL = "INSERT INTO likes (liker_id, liked_id, liked_date) VALUES (:likerId, :likedId, :likedDate)";
    private static final String CHECK_LIKE_EXISTS_SQL = "SELECT EXISTS(SELECT 1 FROM likes WHERE liker_id=:likerId AND liked_id=:likedId)";

    public void createLike(long likerId, long likedId) {
        jdbcTemplate.update(CREATE_LIKE_SQL, Map.of("likerId", likerId, "likedId", likedId, "likedDate", LocalDateTime.now()));
    }

    public boolean checkLikeExists(long likerId, long likedId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(CHECK_LIKE_EXISTS_SQL, Map.of("likerId", likerId, "likedId", likedId), Boolean.class));
    }
}
