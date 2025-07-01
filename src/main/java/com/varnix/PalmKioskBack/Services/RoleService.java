package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Entities.Role;
import com.varnix.PalmKioskBack.Repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found in the database"));
    }

    public void createRole(Role role) {
        roleRepository.save(role);
    }


    @PostConstruct
    public void initDefaultRoles() {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
    }


}
