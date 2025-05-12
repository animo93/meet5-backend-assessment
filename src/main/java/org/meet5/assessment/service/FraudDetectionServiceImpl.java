package org.meet5.assessment.service;

import lombok.AllArgsConstructor;
import org.meet5.assessment.repository.UserRepository;
import org.meet5.assessment.repository.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FraudDetectionServiceImpl implements FraudDetectionService{

    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionServiceImpl.class);

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;

    @Override
    @Scheduled(fixedRate = 60000)
    public void detectFraudulentUsers() {
        logger.info("Fraud detection started");
        // Get all users with status "NEW"
        var newUsers = userRepository.getUserIDsByStatus("NEW");
        logger.debug("New users count: {}", newUsers.size());

        if(!newUsers.isEmpty()){
            // Get the list of fraudulent users from the visit repository
            var fraudulentUsers = visitRepository.getFraudulentUsers(newUsers,10,100);
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
