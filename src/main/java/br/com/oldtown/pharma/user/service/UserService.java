package br.com.oldtown.pharma.user.service;

import br.com.oldtown.pharma.shared.exception.NotFoundException;
import br.com.oldtown.pharma.user.dto.ChangePasswordRequest;
import br.com.oldtown.pharma.user.dto.CreateUserRequest;
import br.com.oldtown.pharma.user.dto.UpdateUserRequest;
import br.com.oldtown.pharma.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    /**
     * Retrieves all users with pagination.
     *
     * @param pageable pagination information
     * @return a paginated list of users
     */
    Page<UserResponse> findAll(Pageable pageable, Boolean active);

    /**
     * Finds a user by its ID.
     *
     * @param id user identifier
     * @return the user found
     * @throws NotFoundException if the user does not exist
     */
    UserResponse findById(Long id);

    /**
     * Finds a user by email.
     *
     * @param email user email
     * @return the user found
     * @throws NotFoundException if no user is found
     */
    UserResponse findByEmail(String email);

    /**
     * Creates a new user.
     *
     * @param user request data for user creation
     * @return the created user
     */
    UserResponse create(CreateUserRequest user);

    /**
     * Updates an existing user.
     *
     * @param id user identifier
     * @param user updated user data
     * @return the updated user
     */
    UserResponse update(Long id, UpdateUserRequest user);

    /**
     * Deletes a user by ID.
     *
     * @param id user identifier
     */
    void delete(Long id);

    /**
     * Changes the user's password.
     *
     * @param id user identifier
     * @param request password change data
     */
    void changePassword(Long id, ChangePasswordRequest request);
}
