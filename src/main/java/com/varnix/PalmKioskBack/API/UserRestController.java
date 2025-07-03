package com.varnix.PalmKioskBack.API;

import com.varnix.PalmKioskBack.Dtos.UserInfoDTO;
import com.varnix.PalmKioskBack.Entities.Role;
import com.varnix.PalmKioskBack.Entities.User;
import com.varnix.PalmKioskBack.Exceptions.AppError;
import com.varnix.PalmKioskBack.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        Optional<User> user = userService.findByUsername(principal.getName());
        List<String> roles = new ArrayList<>();
        for (Role role : user.get().getRoles()) {
            roles.add(role.getName());
        }
        if (user.isEmpty()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "User not found"), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(new UserInfoDTO(user.get().getId(), user.get().getUsername(), user.get().getEmail(), roles));
    }
}
