package br.com.oldtown.pharma.user.service.impl;

import br.com.oldtown.pharma.shared.handler.BusinessException;
import br.com.oldtown.pharma.user.dto.CreateUserRequest;
import br.com.oldtown.pharma.user.dto.UpdateUserRequest;
import br.com.oldtown.pharma.user.dto.UserResponse;
import br.com.oldtown.pharma.user.entity.Role;
import br.com.oldtown.pharma.user.entity.User;
import br.com.oldtown.pharma.user.mapper.UserMapper;
import br.com.oldtown.pharma.user.repository.UserRepository;
import br.com.oldtown.pharma.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return userRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    public List<UserResponse> findAllUsersActive() {
        List<UserResponse> users = userRepository.findByActiveTrue()
                .stream()
                .map(mapper::toResponse)
                .toList();

        if (users.isEmpty()) {
            throw new BusinessException("Users not found.");
        }

        return users;
    }

    @Override
    public UserResponse findById(Long id) {
        Optional<User> userDB = userRepository.findById(id);
        if (userDB.isPresent()) {
            return new UserResponse(userDB.get().getId(),
                    userDB.get().getFirstName(),
                    userDB.get().getLastName(),
                    userDB.get().getEmail());
        } else {
            throw new BusinessException("User not found");
        }
    }

    @Override
    public UserResponse findByEmail(String email) {
        User userDB = userRepository.findByEmail(email);
        if (userDB != null) {
            return new UserResponse(userDB.getId(),
                    userDB.getFirstName(),
                    userDB.getLastName(),
                    userDB.getEmail());
        } else {
            throw new BusinessException("User not found");
        }
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already exists.");
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setActive(true);
        user.setRole(Role.CUSTOMER);

        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getEmail());
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found."));

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered");
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        return mapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found."));
        user.setActive(false);
        userRepository.save(user);
    }
}
