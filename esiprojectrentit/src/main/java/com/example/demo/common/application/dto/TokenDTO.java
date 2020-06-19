package com.example.demo.common.application.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class TokenDTO extends RepresentationModel<TokenDTO> {
    String token;
}
