package com.varnix.PalmKioskBack.Dtos;

import com.varnix.PalmKioskBack.Entities.Role;

import java.util.Collection;

public class UserInfoDTO {
    private Long id;
    private String username;
    private String email;
    private Collection<String> roles;

    public UserInfoDTO(Long id, String username, String email, Collection<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }
}
