package com.taskportal.service;

import com.taskportal.dto.AuthDto;
import com.taskportal.entity.User;
import com.taskportal.exception.GlobalExceptionHandler.DuplicateResourceException;
import com.taskportal.repository.UserRepository;
import com.taskportal.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer handling user registration and authentication.
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Register a new user account with hashed password.
     *
     * @param request Registration details (name, email, password)
     * @return AuthResponse with JWT token and user info
     */
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        // Check for existing email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Build and save new user with hashed password
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        logger.info("Registered new user: {}", user.getEmail());

        String token = jwtService.generateToken(user);

        return AuthDto.AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .message("Registration successful")
                .build();
    }

    /**
     * Authenticate a user and return a JWT token.
     *
     * @param request Login credentials (email, password)
     * @return AuthResponse with JWT token and user info
     */
    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        // Authenticate via Spring Security (throws BadCredentialsException on failure)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        logger.info("User logged in: {}", user.getEmail());

        return AuthDto.AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .message("Login successful")
                .build();
    }
}
