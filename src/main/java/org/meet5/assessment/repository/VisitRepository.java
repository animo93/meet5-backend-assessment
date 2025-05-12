package org.meet5.assessment.repository;

import lombok.AllArgsConstructor;
import org.meet5.assessment.dto.VisitorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class VisitRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(VisitRepository.class);
    private static final String CREATE_VISIT_SQL = "INSERT INTO visits (visitor_id, visited_id, visit_date) VALUES (:visitorId, :visitedId, :visitDate)";
    private static final String GET_ALL_VISITORS_SQL = "SELECT u.name, u.age, v.visit_date FROM users u JOIN visits v ON u.user_id = v.visitor_id WHERE v.visited_id = :id ORDER BY v.visit_date DESC";
    private static final String GET_ALL_FRAUDULENT_USERS_SQL = """
            with visited_and_liked_count_cte as (
            	select u.user_id, count(1) as liked_and_visited_count from users u , visits v, likes l where u.user_id=v.visitor_id and u.user_id=l.liker_id
            	and v.visited_id = l.liked_id and v.visit_date between u.created_at and u.created_at + make_interval(mins => :createdWithin) and u.user_id in (:userIds) group by u.user_id
            )
            select user_id from visited_and_liked_count_cte where liked_and_visited_count >= :allowedLimit
            """;
    private static final String CHECK_VISIT_EXISTS_SQL = "SELECT EXISTS(SELECT 1 FROM visits WHERE visitor_id=:visitorId AND visited_id=:visitedId)";


    @Transactional
    public void createVisit(long visitorId, long visitedId) {
        jdbcTemplate.update(CREATE_VISIT_SQL, Map.of("visitorId", visitorId, "visitedId", visitedId, "visitDate", LocalDateTime.now()));
    }

    public Optional<List<VisitorDTO>> getAllVisitors(long id) {
        List<VisitorDTO> visitors = jdbcTemplate.query(GET_ALL_VISITORS_SQL,
                Map.of("id", id),
                (rs, rowNum) -> new VisitorDTO(rs.getString("name"), rs.getInt("age"), rs.getTimestamp("visit_date").toLocalDateTime()));
        logger.info("Visitors: {}", visitors);
        return Optional.of(visitors);
    }

    public List<Long> getFraudulentUsers(List<Long> newUsers, int createdWithin, int allowedLimit) {
        return jdbcTemplate.query(GET_ALL_FRAUDULENT_USERS_SQL,
                Map.of("userIds", newUsers, "createdWithin", createdWithin, "allowedLimit", allowedLimit),
                (rs, rowNum) -> rs.getLong("user_id"));
    }

    public boolean checkVisitExists(long visitorId, long visitedId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(CHECK_VISIT_EXISTS_SQL, Map.of("visitorId", visitorId, "visitedId", visitedId), Boolean.class));
    }
}
