package com.example.demo.controllers;

import com.example.demo.models.CatalogQueryDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("/catalog/form")
    public String getQueryForm(Model model)	{
        model.addAttribute("catalogQuery",	new CatalogQueryDTO());
        return	"dashboard/catalog/query-form";
    }
}
