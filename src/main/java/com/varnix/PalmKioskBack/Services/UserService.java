package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Dtos.RegistrationUserDto;
import com.varnix.PalmKioskBack.Dtos.UserDTO;
import com.varnix.PalmKioskBack.Entities.Role;
import com.varnix.PalmKioskBack.Entities.User;
import com.varnix.PalmKioskBack.Repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @ReadOnlyProperty
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


    @ReadOnlyProperty
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        getUserRoles(user)
                ));
    }

    @Transactional
    public User createUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setEmail(registrationUserDto.getEmail());

        Role defaultRole = roleService.getUserRole();
        user.setRoles(List.of(defaultRole));

        return userRepository.save(user);
    }

    @ReadOnlyProperty
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @ReadOnlyProperty
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }


    public List<String> getUserRoles(User user) {
        List<String> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roles.add(role.getName());
        }
        return roles;
    }
}
