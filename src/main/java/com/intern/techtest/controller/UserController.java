package com.intern.techtest.controller;

import com.intern.techtest.model.User;
import com.intern.techtest.payload.response.MessageResponse;
import com.intern.techtest.payload.response.UserResponse;
import com.intern.techtest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getNameByUsername(@PathVariable String username) {
        UserResponse userResponse;

        try {
            User user = userService.getUserByUsername(username);
            userResponse = new UserResponse(user.getId(), user.getName());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Bad Credentials: Unauthorized"));
        }

        return ResponseEntity.ok(userResponse);
    }
}
