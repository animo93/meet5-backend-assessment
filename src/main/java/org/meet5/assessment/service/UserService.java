package org.meet5.assessment.service;

import org.meet5.assessment.dao.UserDAO;
import org.meet5.assessment.dto.UserDTO;
import org.meet5.assessment.dto.VisitorDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    void createUser(UserDAO userDAO);

    Map<String, Object> getUser(int id);

    void createVisit(long visitorId, long visitedId);

    void createLike(long likerId, long likedId);

    Optional<List<VisitorDTO>> getAllVisitors(long id);
}
