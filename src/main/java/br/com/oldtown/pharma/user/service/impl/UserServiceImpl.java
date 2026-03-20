package br.com.oldtown.pharma.user.service.impl;

import br.com.oldtown.pharma.config.PasswordEncoderConfig;
import br.com.oldtown.pharma.shared.handler.BusinessException;
import br.com.oldtown.pharma.user.entity.User;
import br.com.oldtown.pharma.user.repository.UserRepository;
import br.com.oldtown.pharma.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoderConfig passwordEncoderConfig) {
        this.userRepository = userRepository;
        this.passwordEncoderConfig = passwordEncoderConfig;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new BusinessException("Users not found.");
        }
        return users;
    }

    @Override
    public User findById(Long id) {
        Optional<User> userDB = userRepository.findById(id);
        return userDB.orElseThrow(() -> new BusinessException("User not found."));
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BusinessException("User not found.");
        } else {
            return user;
        }
    }

    @Override
    public void insert(User user) {
        if (user.getFirstName() == null || user.getFirstName().isEmpty()
                || user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new BusinessException("The first and last name is necessary");
        } else if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new BusinessException("The email is necessary");
        } else if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BusinessException("The password is necessary");
        } else {
            user.setPassword(passwordEncoderConfig.passwordEncoder().encode(user.getPassword()));
            userRepository.save(user);
        }
    }

    @Override
    public void update(Long id, User user) {
        User userBD = findById(id);
        userBD.setFirstName(user.getFirstName());
        userBD.setLastName(user.getLastName());
        userBD.setEmail(user.getEmail());
        userBD.setPassword(user.getPassword());
         userRepository.save(userBD);
    }

    @Override
    public void delete(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }
}
