package br.com.oldtown.pharma.user.dto;

import br.com.oldtown.pharma.user.entity.Role;

public record UserResponse(Long id, String firstName, String lastName, String email, Role role) {
}
