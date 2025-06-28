package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Entities.Role;
import com.varnix.PalmKioskBack.Repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }

}
