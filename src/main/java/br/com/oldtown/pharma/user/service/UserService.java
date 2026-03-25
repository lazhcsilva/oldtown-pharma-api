package br.com.oldtown.pharma.user.service;

import br.com.oldtown.pharma.user.dto.CreateUserRequest;
import br.com.oldtown.pharma.user.dto.UpdateUserRequest;
import br.com.oldtown.pharma.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    List<UserResponse> findAllUsersActive();
    UserResponse findById(Long id);
    UserResponse findByEmail(String email);
    UserResponse create(CreateUserRequest user);
    UserResponse update(Long id, UpdateUserRequest user);
    void delete(Long id);
}
