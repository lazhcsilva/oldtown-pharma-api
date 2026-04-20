package br.com.oldtown.pharma.user.service.impl;

import br.com.oldtown.pharma.shared.exception.BadRequestException;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import br.com.oldtown.pharma.user.dto.ChangePasswordRequest;
import br.com.oldtown.pharma.user.dto.CreateUserRequest;
import br.com.oldtown.pharma.user.dto.UpdateUserRequest;
import br.com.oldtown.pharma.user.dto.UserResponse;
import br.com.oldtown.pharma.user.entity.User;
import br.com.oldtown.pharma.user.mapper.UserMapper;
import br.com.oldtown.pharma.user.repository.UserRepository;
import br.com.oldtown.pharma.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        // Using stream to map entities to DTOs to avoid exposing internal model
        return userRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    public Page<UserResponse> findAllActive(Pageable pageable) {
        // Using stream to map entities to DTOs to avoid exposing internal model
        return userRepository.findByActiveTrue(pageable)
                .map(mapper::toResponse);
    }

    @Override
    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResponse findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists.");
        }

        String passwordCrypt = passwordEncoder.encode(request.password());
        User newUser = mapper.toEntity(request, passwordCrypt);

        return mapper.toResponse(userRepository.save(newUser));
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found."));

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ConflictException("Email already registered");
        }

        mapper.updateEntity(user, request);

        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return mapper.toResponse(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found."));
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        validatePasswords(request, user);

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private void validatePasswords(ChangePasswordRequest request, User user) {
        if (!validatePassword(request.oldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Old password is incorrect.");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new BadRequestException("Passwords do not match.");
        }

        if (validatePassword(request.newPassword(), user.getPasswordHash())) {
            throw new ConflictException("New password must be different from the old password.");
        }

        if (request.newPassword().length() < 8) {
            throw new BadRequestException("Password must have at least 8 characters.");
        }
    }

    private boolean validatePassword(String passwordEntered, String passwordSaveHash) {
        return passwordEncoder.matches(passwordEntered, passwordSaveHash);
    }
}
