package com.buildit.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.hateoas.Link;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class PlantSophio {
    Long _id;
    String name;
    String description;
    BigDecimal price;
    Link _links;

    @JsonProperty("_links")
    public void setLinkss(final Map<String, Link> linksm) {
        linksm.forEach((label, link) ->  this.set_links(link.withRel(label))) ;
    }

}
