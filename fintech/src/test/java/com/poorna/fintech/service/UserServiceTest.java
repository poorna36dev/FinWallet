package com.poorna.fintech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.poorna.fintech.entity.User;
import com.poorna.fintech.exception.BadRequestException;
import com.poorna.fintech.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserEncodesPasswordAndPersistsUser() {
        when(userRepo.existsByUserNameAndEmail("jdoe", "jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser("Jane", "jane@example.com", "jdoe", "secret123");

        assertThat(created.getPassword()).isEqualTo("encoded-password");
        assertThat(created.getUserName()).isEqualTo("jdoe");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void createUserThrowsWhenUserAlreadyExists() {
        when(userRepo.existsByUserNameAndEmail("jdoe", "jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser("Jane", "jane@example.com", "jdoe", "secret123"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User already Exists");
    }

    @Test
    void updatePasswordThrowsWhenOldPasswordIsIncorrect() {
        User user = new User();
        user.setId(1L);
        user.setPassword("stored-hash");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "stored-hash")).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(1L, "wrong-password", "new-password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Old password is incorrect");
    }

    @Test
    void deactivateUserMarksUserInactive() {
        User user = new User();
        user.setId(1L);
        user.setActive(true);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.deactivateUser(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(user.isActive()).isFalse();
    }
}
