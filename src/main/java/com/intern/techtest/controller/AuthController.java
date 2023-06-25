package com.intern.techtest.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intern.techtest.model.ERole;
import com.intern.techtest.model.Role;
import com.intern.techtest.model.User;
import com.intern.techtest.model.RefreshToken;
import com.intern.techtest.payload.request.LoginRequest;
import com.intern.techtest.payload.request.SignupRequest;
import com.intern.techtest.payload.request.TokenRefreshRequest;
import com.intern.techtest.payload.request.LogoutRequest;
import com.intern.techtest.payload.response.JwtResponse;
import com.intern.techtest.payload.response.MessageResponse;
import com.intern.techtest.payload.response.TokenRefreshResponse;
import com.intern.techtest.repository.RoleRepository;
import com.intern.techtest.repository.UserRepository;
import com.intern.techtest.security.jwt.JwtUtils;
import com.intern.techtest.security.services.UserDetailsImpl;
import com.intern.techtest.security.services.RefreshTokenService;
import com.intern.techtest.exception.TokenRefreshException;
import com.intern.techtest.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication= authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Bad Credentials: Unauthorized"));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken.getToken(),
                userDetails.getUsername(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userService.cekExistsUserByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
        }

        userService.createUser(signUpRequest);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefresh();
        String token;

        try {
            Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByToken(requestRefreshToken);
            if (optionalRefreshToken.isPresent()) {
                RefreshToken refreshToken = refreshTokenService.verifyExpiration(optionalRefreshToken.get());
                User user = refreshToken.getUser();
                token = jwtUtils.generateTokenFromUsername(user.getUsername());
            } else {
                throw new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!");
            }
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        }

        return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogoutRequest logoutRequest) {
        try {
            User user = userService.getUserByUsername(logoutRequest.getUsername());
            String userId = user.getId();
            refreshTokenService.deleteByUserId(userId);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        }

        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}
