package org.meet5.assessment.controller;

import lombok.AllArgsConstructor;
import org.meet5.assessment.dao.UserDAO;
import org.meet5.assessment.dto.UserDTO;
import org.meet5.assessment.dto.VisitorDTO;
import org.meet5.assessment.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1/users")
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @PostMapping
    public void createUser(@RequestBody UserDTO userDTO) {
        var userDAO = new UserDAO();
        userDAO.setName(userDTO.name());
        userDAO.setAge(userDTO.age());
        userService.createUser(userDAO);
    }

    @PutMapping("/{id}/visit")
    public void createVisit(@PathVariable("id") long visitedId,@RequestParam("visitorId") long visitorId) {
        userService.createVisit(visitorId, visitedId);
    }

    @PutMapping("/{id}/like")
    public void createLike(@PathVariable("id") long likedId, @RequestParam("likerId") long likerId) {
        userService.createLike(likerId, likedId);
    }

    @GetMapping("/{id}/visitors")
    public Optional<List<VisitorDTO>> getAllVisitors(@PathVariable("id") long id) {
        return userService.getAllVisitors(id);
    }

}
