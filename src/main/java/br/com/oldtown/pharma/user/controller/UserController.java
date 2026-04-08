package br.com.oldtown.pharma.user.controller;

import br.com.oldtown.pharma.user.dto.CreateUserRequest;
import br.com.oldtown.pharma.user.dto.UpdateUserRequest;
import br.com.oldtown.pharma.user.dto.UserResponse;
import br.com.oldtown.pharma.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Operations related to users")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping
    public ResponseEntity<PagedModel<UserResponse>> findAll(
            @Parameter(hidden = true) Pageable pageable) {
        Page<UserResponse> page = userService.findAll(pageable);
        return ResponseEntity.ok(new PagedModel<>(page));
    }

    @Operation(summary = "Get all users active")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/actives")
    public ResponseEntity<List<UserResponse>> findAllUsersActive(
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(userService.findAllActive());
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Get user by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/search")
    public ResponseEntity<UserResponse> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @Operation(summary = "Insert a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @Operation(summary = "Update user")
    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<UserResponse> update(@PathVariable @Positive Long id,
                                               @Valid @RequestBody UpdateUserRequest user) {
        return ResponseEntity.ok(userService.update(id, user));
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
