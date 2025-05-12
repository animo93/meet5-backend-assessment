package org.meet5.assessment.service;

import org.meet5.assessment.repository.UserRepository;
import org.meet5.assessment.repository.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FraudDetectionServiceImpl implements FraudDetectionService{

    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionServiceImpl.class);

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final int createdWithin;
    private final int allowedLimit;

    public FraudDetectionServiceImpl(@Value("${fraud.user.config.created_within}") int createdWithin,
                                     @Value("${fraud.user.config.allowed_limit}") int allowedLimit,
                                     VisitRepository visitRepository,
                                     UserRepository userRepository) {
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
        this.createdWithin = createdWithin;
        this.allowedLimit = allowedLimit;
    }

    @Override
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void detectFraudulentUsers() {
        logger.info("Fraud detection started");
        // Get all users with status "NEW"
        var newUsers = userRepository.getUserIDsByStatus("NEW");
        logger.debug("New users count: {}", newUsers.size());

        if(!newUsers.isEmpty()){
            // Get the list of fraudulent users from the visit repository
            var fraudulentUsers = visitRepository.getFraudulentUsers(newUsers,createdWithin,allowedLimit);
            logger.debug("Fraudulent users count: {}", fraudulentUsers.size());

            for (var userId : newUsers) {
                if(fraudulentUsers.contains(userId)) {
                    // Update the status of fraudulent users to "FRAUD"
                    userRepository.updateUserStatus(userId, "FRAUD");
                }else{
                    // Update the status of non-fraudulent users to "VALID"
                    userRepository.updateUserStatus(userId, "VALID");
                }
            }
        }

        logger.info("Fraud detection completed");
    }
}
