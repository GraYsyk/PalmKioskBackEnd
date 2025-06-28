package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Dtos.JwtRequest;
import com.varnix.PalmKioskBack.Dtos.JwtResponse;
import com.varnix.PalmKioskBack.Dtos.RegistrationUserDto;
import com.varnix.PalmKioskBack.Dtos.UserDTO;
import com.varnix.PalmKioskBack.Entities.User;
import com.varnix.PalmKioskBack.Exceptions.AppError;
import com.varnix.PalmKioskBack.Utils.JWTTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component
public class AuthService {
    private final UserService userService;
    private final JWTTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserService userService,
                          JWTTokenUtils jwtTokenUtils,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid data"), HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        String refreshToken = jwtTokenUtils.generateRefreshToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(new AppError(HttpStatus.BAD_REQUEST.value(), "Refresh token is missing"));
        }

        String username;
        try {
            username = jwtTokenUtils.getUsernameFromToken(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token"));
        }

        UserDetails userDetails = userService.loadUserByUsername(username);
        if (!jwtTokenUtils.getUsernameFromToken(refreshToken).equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired refresh token"));
        }

        String newAccessToken = jwtTokenUtils.generateToken(userDetails);
        String newRefreshToken = jwtTokenUtils.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationUserDto registrationUserDto) {
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Passwords do not match"), HttpStatus.UNAUTHORIZED);
        }
        if(userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Username already exists"), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createUser(registrationUserDto);
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getEmail()));
    }
}
