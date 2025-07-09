package com.varnix.PalmKioskBack.API;

import com.varnix.PalmKioskBack.Dtos.RegistrationUserDto;
import com.varnix.PalmKioskBack.Dtos.UserDTO;
import com.varnix.PalmKioskBack.Entities.User;
import com.varnix.PalmKioskBack.Exceptions.AppError;
import com.varnix.PalmKioskBack.Services.RoleService;
import com.varnix.PalmKioskBack.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService,  RoleService roleService,@Lazy PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    //USER

    @Operation(summary = "Create request like: GET /admin/users?page=0&size=10&sort=username,asc (asc - Ascending)\n")
    @GetMapping("/users")
    public Page<UserDTO> getAll(
            @ParameterObject Pageable pageable) {
        return userService.findAll(pageable);
    }

    @PostMapping("/users/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody RegistrationUserDto user) {
        User createdUser = userService.createUser(user);

        UserDTO response = new UserDTO(
                createdUser.getId(),
                createdUser.getUsername(),
                "PRIVATE",
                createdUser.getEmail(),
                userService.getUserRoles(createdUser)
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Method automatically detects user's role and either grants or removes admin role.")
    @PatchMapping("/users/grant")
    public ResponseEntity<?> toggleAdminTole(@RequestParam Long id) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "User with id " + id + " was not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        User user = userOpt.get();
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
        if (!isAdmin) {
            user.setRoles(new ArrayList<>(List.of(roleService.getUserRole(), roleService.getAdminRole())));
        } else {
            user.setRoles(new ArrayList<>(List.of(roleService.getUserRole())));
        }

        userService.save(user);
        UserDTO response = new UserDTO(
                user.getId(),
                user.getUsername(),
                "PRIVATE",
                user.getEmail(),
                userService.getUserRoles(user)
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/remove")
    public ResponseEntity<?> removeUser(@RequestParam Long id) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "User with id " + id + " was not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        User user = userOpt.get();
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));

        if (isAdmin) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "Cannot delete user with admin role. Remove the role first."),
                    HttpStatus.BAD_REQUEST
            );
        }

        userService.delete(user.getId());

        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @Operation(summary = "Update user's username, email, and password by user id")
    @PatchMapping("/users/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO updateDto) {
        Optional<User> userOpt = userService.findById(updateDto.getId());
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "User with id " + updateDto.getId() + " was not found"),
                    HttpStatus.NOT_FOUND
            );
        }

        User user = userOpt.get();

        // Проверка на дублирующий username
        if (updateDto.getUsername() != null && !updateDto.getUsername().isBlank()) {
            Optional<User> existing = userService.findByUsername(updateDto.getUsername());
            if (existing.isPresent() && !existing.get().getId().equals(user.getId())) {
                return new ResponseEntity<>(
                        new AppError(HttpStatus.CONFLICT.value(), "Username is already taken"),
                        HttpStatus.CONFLICT
                );
            }
            user.setUsername(updateDto.getUsername());
        }

        // Проверка на дублирующий email (если у тебя есть метод findByEmail)
        if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank()) {
            Optional<User> existingByEmail = userService.findByEmail(updateDto.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(user.getId())) {
                return new ResponseEntity<>(
                        new AppError(HttpStatus.CONFLICT.value(), "Email is already taken"),
                        HttpStatus.CONFLICT
                );
            }
            user.setEmail(updateDto.getEmail());
        }

        // Обновление пароля
        if (updateDto.getPassword() != null && !updateDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        userService.save(user);

        UserDTO response = new UserDTO(
                user.getId(),
                user.getUsername(),
                "PRIVATE",
                user.getEmail(),
                userService.getUserRoles(user)
        );

        return ResponseEntity.ok(response);
    }
}
