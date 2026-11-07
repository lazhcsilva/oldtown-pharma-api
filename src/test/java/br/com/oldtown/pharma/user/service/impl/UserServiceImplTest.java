package br.com.oldtown.pharma.user.service.impl;

import br.com.oldtown.pharma.shared.exception.BadRequestException;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import br.com.oldtown.pharma.user.dto.ChangePasswordRequest;
import br.com.oldtown.pharma.user.dto.UpdateUserRequest;
import br.com.oldtown.pharma.user.entity.Role;
import br.com.oldtown.pharma.user.entity.User;
import br.com.oldtown.pharma.user.mapper.UserMapper;
import br.com.oldtown.pharma.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock private UserRepository repository;
    @Mock private PasswordEncoder encoder;
    private UserServiceImpl service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(repository, encoder, Mappers.getMapper(UserMapper.class));
        user = new User("Original", "Name", "original@example.com");
        user.setId(1L);
        user.setPasswordHash("stored-hash");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
    }

    @Test
    void shouldUpdateProfileWithoutChangingCredentialsOrAccountState() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);
        var passwordChangedAt = user.getPasswordChangedAt();
        UpdateUserRequest request = new UpdateUserRequest("Updated", "Surname", "updated@example.com");

        var result = service.update(1L, request);

        assertThat(result.firstName()).isEqualTo(request.firstName());
        assertThat(result.lastName()).isEqualTo(request.lastName());
        assertThat(result.email()).isEqualTo(request.email());
        assertThat(user.getPasswordHash()).isEqualTo("stored-hash");
        assertThat(user.getPasswordChangedAt()).isEqualTo(passwordChangedAt);
        assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(user.isActive()).isTrue();
        assertThat(user.isLocked()).isTrue();
        verify(repository).save(user);
        verifyNoInteractions(encoder);
    }

    @Test
    void shouldUpdateProfileWithUnchangedEmail() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);

        service.update(1L, new UpdateUserRequest("Updated", "Surname", user.getEmail()));

        verify(repository, never()).existsByEmail(anyString());
        assertThat(user.getPasswordHash()).isEqualTo("stored-hash");
        verifyNoInteractions(encoder);
    }

    @Test
    void shouldRejectDuplicateEmailWithoutChangingUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.update(1L,
                new UpdateUserRequest("Updated", "Surname", "duplicate@example.com")))
                .isInstanceOf(ConflictException.class);

        assertThat(user.getFirstName()).isEqualTo("Original");
        assertThat(user.getEmail()).isEqualTo("original@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("stored-hash");
        verify(repository, never()).save(any());
        verifyNoInteractions(encoder);
    }

    @Test
    void shouldRejectProfileUpdateForMissingUser() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L,
                new UpdateUserRequest("Updated", "Surname", "updated@example.com")))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any());
        verifyNoInteractions(encoder);
    }

    @Test
    void shouldChangePasswordThroughDedicatedOperation() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.matches("Current123!", "stored-hash")).thenReturn(true);
        when(encoder.encode("NewPass123!")).thenReturn("new-hash");

        service.changePassword(1L, new ChangePasswordRequest("Current123!", "NewPass123!", "NewPass123!"));

        assertThat(user.getPasswordHash()).isEqualTo("new-hash");
        verify(repository).save(user);
    }

    @Test
    void shouldRejectWrongCurrentPasswordWithoutChangingHash() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.changePassword(1L,
                new ChangePasswordRequest("Wrong123!", "NewPass123!", "NewPass123!")))
                .isInstanceOf(BadRequestException.class).hasMessage("Old password is incorrect.");

        assertPasswordWasNotChanged();
    }

    @Test
    void shouldRejectMismatchedConfirmation() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.matches("Current123!", "stored-hash")).thenReturn(true);

        assertThatThrownBy(() -> service.changePassword(1L,
                new ChangePasswordRequest("Current123!", "NewPass123!", "Different123!")))
                .isInstanceOf(BadRequestException.class).hasMessage("Passwords do not match.");

        assertPasswordWasNotChanged();
    }

    @Test
    void shouldRejectReuseOfCurrentPassword() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.matches("Current123!", "stored-hash")).thenReturn(true);

        assertThatThrownBy(() -> service.changePassword(1L,
                new ChangePasswordRequest("Current123!", "Current123!", "Current123!")))
                .isInstanceOf(ConflictException.class);

        assertPasswordWasNotChanged();
    }

    @Test
    void shouldRejectNewPasswordBelowEightCharacters() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.matches("Current123!", "stored-hash")).thenReturn(true);

        assertThatThrownBy(() -> service.changePassword(1L,
                new ChangePasswordRequest("Current123!", "Short12", "Short12")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Password must have at least 8 characters.");

        assertPasswordWasNotChanged();
    }

    private void assertPasswordWasNotChanged() {
        assertThat(user.getPasswordHash()).isEqualTo("stored-hash");
        verify(repository, never()).save(any());
        verify(encoder, never()).encode(anyString());
    }
}
