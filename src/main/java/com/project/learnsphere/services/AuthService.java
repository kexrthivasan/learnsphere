package com.project.learnsphere.services;
import com.project.learnsphere.models.Role;
import com.project.learnsphere.models.User;
import com.project.learnsphere.repositories.UserRepository;
import com.project.learnsphere.security.JwtUtil;
import com.project.learnsphere.dtos.auth.LoginRequest;
import com.project.learnsphere.dtos.auth.SignUpRequest;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
    }

    /**
     * Registers a new user from SignUpRequest DTO.
     */
    public User signUp(SignUpRequest signUpRequest) {
        // Check if email is already taken
        userRepository.findByEmail(signUpRequest.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email already in use."); });

        // Map DTO to Entity
        User user = modelMapper.map(signUpRequest, User.class);

        // Encode password
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Security: prevent public registration of ADMIN role
        if (signUpRequest.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("ADMIN role cannot be assigned during registration.");
        }
        // Default role to STUDENT when not provided
        user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.STUDENT);

        return userRepository.save(user);
    }

    /**
     * Authenticates user and returns JWT token.
     */
    public String login(LoginRequest loginRequest) {
        // Authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Fetch user from DB
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        // Create UserDetails for JWT
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        // Generate token
        return jwtUtil.generateToken(userDetails);
    }
}
