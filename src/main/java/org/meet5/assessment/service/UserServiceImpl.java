package org.meet5.assessment.service;

import lombok.AllArgsConstructor;
import org.meet5.assessment.dao.UserDAO;
import org.meet5.assessment.dto.UserDTO;
import org.meet5.assessment.dto.VisitorDTO;
import org.meet5.assessment.repository.LikeRepository;
import org.meet5.assessment.repository.UserRepository;
import org.meet5.assessment.repository.VisitRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final VisitRepository visitRepository;
    private final LikeRepository likeRepository;

    @Override
    public void createUser(final UserDAO userDAO) {
        userRepository.createUser(userDAO.getName(), userDAO.getAge(), "NEW");
    }

    @Override
    public Map<String, Object> getUser(int id) {
        return Map.of();
    }

    //TODO: Add controller exception handling
    @Override
    public void createVisit(long visitorId, long visitedId) {
        try {
            // Check if visiting yourself
            if(visitorId == visitedId)
                throw new IllegalArgumentException("You cannot visit yourself");

            // Check if visit exists
            var existingVisit = visitRepository.checkVisitExists(visitorId,visitedId);
            if(!existingVisit)
                visitRepository.createVisit(visitorId, visitedId);
        }catch (Exception e){
            throw new RuntimeException("Unable to create visit", e);
        }

    }


    @Override
    public void createLike(long likerId, long likedId) {
        // Check if liking yourself
        if(likerId == likedId)
            throw new IllegalArgumentException("You cannot like yourself");

        // Check if like exists
        var existingLike = likeRepository.checkLikeExists(likerId, likedId);
        if(!existingLike)
            likeRepository.createLike(likerId, likedId);
    }

    @Override
    public Optional<List<VisitorDTO>> getAllVisitors(long id) {
        return visitRepository.getAllVisitors(id);
    }
}
