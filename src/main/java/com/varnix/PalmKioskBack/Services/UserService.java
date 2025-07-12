package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Dtos.RegistrationUserDto;
import com.varnix.PalmKioskBack.Dtos.UserDTO;
import com.varnix.PalmKioskBack.Entities.Role;
import com.varnix.PalmKioskBack.Entities.User;
import com.varnix.PalmKioskBack.Repositories.UserRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;

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

    public Page<UserDTO> searchUsers(String username, String email, Boolean isAdmin, Pageable pageable) {
        return userRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (username != null && !username.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
            }

            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if (isAdmin != null) {
                query.distinct(true);
                Join<User, Role> roleJoin = root.join("roles");

                if (isAdmin) {
                    predicates.add(cb.equal(cb.lower(roleJoin.get("name")), "role_admin"));
                } else {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<User> subRoot = subquery.from(User.class);
                    Join<User, Role> subRoleJoin = subRoot.join("roles");
                    subquery.select(subRoot.get("id"))
                            .where(cb.and(
                                    cb.equal(subRoot.get("id"), root.get("id")),
                                    cb.equal(cb.lower(subRoleJoin.get("name")), "role_admin")
                            ));

                    predicates.add(cb.not(cb.exists(subquery)));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(user -> new UserDTO(
                user.getId(),
                user.getUsername(),
                "PRIVATE",
                user.getEmail(),
                getUserRoles(user)
        ));
    }





    @ReadOnlyProperty
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        "PRIVATE",
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
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
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
