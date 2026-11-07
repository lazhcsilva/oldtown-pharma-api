package br.com.oldtown.pharma.user.controller;

import br.com.oldtown.pharma.shared.handler.GlobalExceptionHandler;
import br.com.oldtown.pharma.user.entity.Role;
import br.com.oldtown.pharma.user.entity.User;
import br.com.oldtown.pharma.user.mapper.UserMapper;
import br.com.oldtown.pharma.user.repository.UserRepository;
import br.com.oldtown.pharma.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserProfileUpdateTest {
    private MockMvc mockMvc;
    private UserRepository repository;
    private PasswordEncoder encoder;
    private User user;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        encoder = mock(PasswordEncoder.class);
        var service = new UserServiceImpl(repository, encoder, Mappers.getMapper(UserMapper.class));
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        user = new User("Original", "Name", "original@example.com");
        user.setId(1L);
        user.setRole(Role.CUSTOMER);
        user.setPasswordHash("stored-hash");
    }

    @Test
    void shouldUpdateProfileWithoutRequiringPassword() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);

        mockMvc.perform(put("/api/v1/users/1").contentType(MediaType.APPLICATION_JSON).content("""
                {"firstName":"Updated","lastName":"Surname","email":"updated@example.com"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("firstName").value("Updated"))
                .andExpect(jsonPath("email").value("updated@example.com"))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("passwordHash").doesNotExist());

        assertThat(user.getPasswordHash()).isEqualTo("stored-hash");
        verifyNoInteractions(encoder);
    }

    @Test
    void shouldNotChangePasswordFromExtraProfileFields() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);

        mockMvc.perform(put("/api/v1/users/1").contentType(MediaType.APPLICATION_JSON).content("""
                {"firstName":"Updated","lastName":"Surname","email":"updated@example.com",
                 "password":"Replacement123!","passwordHash":"injected-hash"}
                """))
                .andExpect(status().isOk());

        assertThat(user.getPasswordHash()).isEqualTo("stored-hash");
        verifyNoInteractions(encoder);
    }

    @Test
    void shouldRejectInvalidProfileWithoutSaving() throws Exception {
        mockMvc.perform(put("/api/v1/users/1").contentType(MediaType.APPLICATION_JSON).content("""
                {"firstName":"","lastName":"Surname","email":"invalid"}
                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(repository, encoder);
    }

    @Test
    void shouldKeepDedicatedPasswordChangeAvailable() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.matches("Current123!", "stored-hash")).thenReturn(true);
        when(encoder.encode("NewPass123!")).thenReturn("new-hash");

        mockMvc.perform(patch("/api/v1/users/1/password").contentType(MediaType.APPLICATION_JSON).content("""
                {"oldPassword":"Current123!","newPassword":"NewPass123!","confirmNewPassword":"NewPass123!"}
                """))
                .andExpect(status().isOk());

        assertThat(user.getPasswordHash()).isEqualTo("new-hash");
        verify(repository).save(user);
    }
}
