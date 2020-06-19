package com.example.demo.sales.web;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.inventory.application.dto.CatalogQueryDTO;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    InventoryService inventoryService;

    @GetMapping("/catalog/form")
    public String getQueryForm(Model model)	{
        model.addAttribute("catalogQuery",	new CatalogQueryDTO());
        return	"dashboard/catalog/query-form";
    }

    @PostMapping("/catalog/query")
    public String getQueryResults(CatalogQueryDTO query, Model model) {
        CollectionModel<PlantInventoryEntryDTO> plants = inventoryService.findAvailablePlants(query.getName(),
                query.getRentalPeriod().getStartDate(),
                query.getRentalPeriod().getEndDate());
        model.addAttribute("plants", plants);

        PurchaseOrderDTO po = new PurchaseOrderDTO();
        po.setRentalPeriod(query.getRentalPeriod());
        model.addAttribute("po", po);

        return	"dashboard/catalog/query-result";
    }
}
