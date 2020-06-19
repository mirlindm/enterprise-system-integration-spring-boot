package com.buildit.Auth.application.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class UserDto extends RepresentationModel<UserDto> {

    private String username;
    private String password;
    private int age;
    private int salary;
    private Long role_id;

}
