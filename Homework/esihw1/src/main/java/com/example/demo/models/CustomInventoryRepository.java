package com.example.demo.models;



import javafx.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CustomInventoryRepository {
    List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate);

    List<Pair<String, BigDecimal>> query1();
    List<Pair<String, Long>> query2(LocalDate startDate, LocalDate endDate);
    List<Pair<String, Long>> query3(String plantName,LocalDate startDate, LocalDate endDate);
    List<String> query4();

}
