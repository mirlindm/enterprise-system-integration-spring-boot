package com.buildit.integration;

import lombok.Data;
import org.springframework.hateoas.Link;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Plant {
    Long _id;
    String name;
    String description;
    BigDecimal price;
    BigDecimal totalPrice;
    List<Link> links;
}

