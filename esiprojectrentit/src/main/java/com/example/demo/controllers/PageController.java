package com.example.demo.controllers;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.controllers.models.MaintenanceTaskFormDTO;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.maintenance.application.dto.MaintenanceTaskDTO;
import com.example.demo.maintenance.application.service.MaintenanceService;
import com.example.demo.maintenance.domain.model.MaintenanceTask;
import com.example.demo.maintenance.domain.model.MaintenanceTaskRequest;
import com.example.demo.maintenance.domain.model.TypeOfWork;
import com.example.demo.maintenance.domain.repository.MaintenanceTaskRepository;
import com.example.demo.sales.application.services.SalesService;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
public class PageController {
    @Autowired
    PurchaseOrderRepository poRepo;

    @Autowired
    MaintenanceTaskRepository taskRepo;

    @Autowired
    SalesService salesService;

    @Autowired
    MaintenanceService maintService;

    @GetMapping()
    public String index(Model model, HttpSession session){
        System.out.println("===> index");

        return "redirect:/po-list";
    }

    @GetMapping("/po-list")
    public String listPOs(Model model, HttpSession session){
        System.out.println("===> po-list");
        System.out.println("===> Model: "+model);

        List<PurchaseOrder> poList = poRepo.findAll();
//        System.out.println("===> poList: "+poList);

        model.addAttribute("poList",poList);
        return "purchase_order_list";
    }

    @GetMapping("/po-accepted/{poId}")
    public String acceptPo(@PathVariable Long poId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Accept po");
        System.out.println("===> poId: "+poId);

        try{
            salesService.acceptPO(poId);
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());

            return listPOs(model, session); // to keep error message
        }

        return "redirect:/po-list";
    }

    @GetMapping("/po-rejected/{poId}")
    public String rejectPo(@PathVariable Long poId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Reject po");
        System.out.println("===> poId: "+poId);

        try{
            salesService.rejectPO(poId);
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());

            return listPOs(model, session); // to keep error message
        }

        return "redirect:/po-list";
    }

    @GetMapping("/po-rejected-by-customer/{poId}")
    public String rejectedByCustomerPo(@PathVariable Long poId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Reject by customer po");
        System.out.println("===> poId: "+poId);

        try{
            salesService.plantRejected(poId);
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());

            return listPOs(model, session); // to keep error message
        }

        return "redirect:/po-list";
    }

    @GetMapping("/po-dispatched/{poId}")
    public String dispatchPo(@PathVariable Long poId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Dispatch po");
        System.out.println("===> poId: "+poId);

        try{
            salesService.plantDispatched(poId);
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());

            return listPOs(model, session); // to keep error message
        }

        return "redirect:/po-list";
    }

    @GetMapping("/po-delivered/{poId}")
    public String deliverPo(@PathVariable Long poId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Deliver po");
        System.out.println("===> poId: "+poId);

        try{
            salesService.plantDelivered(poId);
        }catch(Exception e){
            System.out.println("Returning went wrong");
            model.addAttribute("errorMessage",e.getMessage());

            return listPOs(model, session); // to keep error message
        }

        return "redirect:/po-list";
    }

    @GetMapping("/po-returned/{poId}")
    public String returnedPo(@PathVariable Long poId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Return po");
        System.out.println("===> poId: "+poId);

        try{
            salesService.plantReturned(poId);
        }catch(Exception e){
            System.out.println("Returning went wrong: "+e.getMessage());
            model.addAttribute("errorMessage",e.getMessage());

            return listPOs(model, session); // to keep error message
        }

        return "redirect:/po-list";
    }

    @GetMapping("/maintenance-list")
    public String maintenanceList(Model model, HttpSession session) throws Exception {
        System.out.println("===> maintenance list");

        List<MaintenanceTask> taskList = taskRepo.findAll();
        System.out.println("===> all tasks: "+taskList);

        model.addAttribute("taskList",taskList);
        return "maintenance_task_list";
    }

    @GetMapping("/maintenance")
    public String createMaintenance(Model model, HttpSession session) throws Exception {
        System.out.println("===> create maintenance");

        MaintenanceTaskFormDTO taskFormDTO = new MaintenanceTaskFormDTO();
        MaintenanceTaskRequest taskReq = new MaintenanceTaskRequest();
        taskReq.setType_of_work(TypeOfWork.CORRECTIVE);
        taskReq.setRentalPeriod(BusinessPeriodDTO.of(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)));
        taskFormDTO.setMaintenanceTaskRequest(taskReq);
        System.out.println("===> gonna add taskformdto to model");

        model.addAttribute("taskFormDTO",taskFormDTO);
        return "maintenance";
    }

    @PostMapping("/maintenance")
    public String saveMaintenance(MaintenanceTaskFormDTO taskFormDTO, Model model, HttpSession session) throws Exception {
        System.out.println("===> save maintenance");
        System.out.println("===> taskFormDTO: "+taskFormDTO);

        try{
            maintService.createMaintenanceTask(taskFormDTO.getPlantItemId(),taskFormDTO.getMaintenanceTaskRequest());
        }catch (Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        System.out.println("===> All maint tasks: "+taskRepo.findAll());

        model.addAttribute("taskFormDTO", taskFormDTO);
        return "redirect:/maintenance-list";
    }
}
