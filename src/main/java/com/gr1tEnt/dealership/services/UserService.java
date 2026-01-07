package com.gr1tEnt.dealership.services;

import com.gr1tEnt.dealership.exception.UserAlreadyExistsException;
import com.gr1tEnt.dealership.models.RegisterDto;
import com.gr1tEnt.dealership.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public User registerUser(RegisterDto registerDto) {

        if (userRepository.existsByUsername(registerDto.getUsername()) &&
                userRepository.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword()))
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

}
