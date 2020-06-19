package com.example.demo.common.application.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class AuthDTO extends RepresentationModel<AuthDTO> {
    String username;
    String password;
}

