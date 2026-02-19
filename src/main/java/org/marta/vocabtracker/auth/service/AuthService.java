package org.marta.vocabtracker.auth.service;

import lombok.RequiredArgsConstructor;
import org.marta.vocabtracker.auth.dto.AuthResponse;
import org.marta.vocabtracker.auth.dto.LoginRequest;
import org.marta.vocabtracker.auth.dto.RegisterRequest;
import org.marta.vocabtracker.jwt.JWTService;
import org.marta.vocabtracker.user.model.Role;
import org.marta.vocabtracker.user.repository.UserRepository;
import org.marta.vocabtracker.user.model.UserEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);


        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getEmail());
    }
}
