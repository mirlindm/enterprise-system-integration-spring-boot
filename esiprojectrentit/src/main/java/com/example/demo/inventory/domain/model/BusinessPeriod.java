package com.example.demo.inventory.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
@Value
@NoArgsConstructor(force=true)
@AllArgsConstructor(staticName="of")
public class BusinessPeriod {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate endDate;
//
//    public BusinessPeriod(){
//
//    }

    public Boolean overlapsWith(BusinessPeriod p2){
        return this.includes(p2.startDate)
                || this.includes(p2.endDate)
                || p2.includes(this.startDate)
                || p2.includes(this.endDate)
                || (this.startDate == p2.startDate && this.endDate == p2.endDate);
    }

    public Boolean includes(LocalDate dt){
        return dt.isAfter(this.startDate) && dt.isBefore(this.endDate);
    }
}
