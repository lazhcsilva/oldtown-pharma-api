package br.com.oldtown.pharma.user.mapper;

import br.com.oldtown.pharma.user.dto.UserResponse;
import br.com.oldtown.pharma.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}
