package br.com.oldtown.pharma.user.service;

import br.com.oldtown.pharma.user.dto.CreateUserRequest;
import br.com.oldtown.pharma.user.dto.UpdateUserRequest;
import br.com.oldtown.pharma.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<UserResponse> findAll(Pageable pageable);
    List<UserResponse> findAllUsersActive();
    UserResponse findById(Long id);
    UserResponse findByEmail(String email);
    UserResponse create(CreateUserRequest user);
    UserResponse update(Long id, UpdateUserRequest user);
    void delete(Long id);
}
