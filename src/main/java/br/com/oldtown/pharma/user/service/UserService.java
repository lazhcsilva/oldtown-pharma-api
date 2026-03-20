package br.com.oldtown.pharma.user.service;

import br.com.oldtown.pharma.user.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User findByEmail(String email);
    void insert(User user);
    void update(Long id, User user);
    void delete(Long id);
}
