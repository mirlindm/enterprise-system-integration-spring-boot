package com.buildit.Auth.rest;

import com.buildit.Auth.application.dto.UserDto;
import com.buildit.Auth.application.service.UserService;
import com.buildit.Auth.domain.model.Role;
import com.buildit.Auth.domain.model.User;
import com.buildit.Auth.domain.repository.RoleRepository;
import com.buildit.Auth.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;


    //@Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/user", method = RequestMethod.GET)
    public List listUser(){
        return userRepository.findAll();
    }

    //@Secured("ROLE_USER")
    //@PreAuthorize("hasRole('USER')")
    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User getOne(@PathVariable(value = "id") Long id){
        return userRepository.findById(id).orElse(null);
    }

    @RequestMapping(value="/signup", method = RequestMethod.POST)
    public User create(@RequestBody UserDto userDTO){
        User user = new User();
        user.setAge(userDTO.getAge());
        user.setSalary(userDTO.getSalary());
        user.setUsername(userDTO.getUsername());
        user.setPassword(bcryptEncoder.encode(userDTO.getPassword()));
        List<Role> r = new ArrayList<>();
        r.add(roleRepository.findById(userDTO.getRole_id()).orElse(null));
        user.setRoles(r);
        user = userRepository.save(user);

        return user;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/user/{id}", method = RequestMethod.DELETE)
    public User deleteUser(@PathVariable(value = "id") Long id){
        User user = userRepository.findById(id).orElse(null);
        userRepository.delete(user);
        return new User();
    }

}
