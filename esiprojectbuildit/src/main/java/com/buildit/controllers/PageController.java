package com.buildit.controllers;

import com.buildit.Auth.domain.model.LoginUser;
import com.buildit.common.domain.model.BusinessPeriodDTO;
import com.buildit.common.rest.ExtendedLink;
import com.buildit.controllers.models.CatalogQueryDTO;
import com.buildit.integration.IntegrationService;
import com.buildit.payables.application.dto.InvoiceDTO;
import com.buildit.payables.application.services.PayablesService;
import com.buildit.payables.domain.model.InvStatus;
import com.buildit.payables.domain.model.Invoice;
import com.buildit.payables.domain.repositories.InvoiceRepository;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.application.service.PlantHiringService;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.domain.model.POStatus;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import com.buildit.rental.domain.repositories.PurchaseOrderRepository;
import org.apache.xpath.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PageController {
    @Autowired
    IntegrationService integrationService;

    @Autowired
    RentalService rentalService;

    @Autowired
    PlantHiringService plantHiringService;

    @Autowired
    PlantHireRequestRepository phrRepo;

    @Autowired
    PurchaseOrderRepository poRepo;

    @Autowired
    InvoiceRepository invoiceRepo;

    @Autowired
    PayablesService payablesService;

    @Autowired
    private AuthenticationManager authenticationManager;
    private POStatus poStatus;

    @GetMapping("/login")
    public String login(Model model, HttpSession session){
        LoginUser user = new LoginUser();

        model.addAttribute("user",user);
        return "login";
    }

    @PostMapping("/login")
    public String login(LoginUser user, Model model, HttpSession session){


        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("user",user);
        return index(model, session);
    }

    // BuildIT Authentication
    // user1/password1 --> Role ADMIN
    // user2/password2 --> Role SITE
    // user3/password3 --> Role WORKS

    @GetMapping()
    public String index(Model model, HttpSession session){
        System.out.println("===> index");
        System.out.println("===> session: "+session);


        CatalogQueryDTO catalogQueryDTO = new CatalogQueryDTO();
        catalogQueryDTO.setRentalPeriod(BusinessPeriodDTO.of(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)));

        model.addAttribute("catalogQuery", catalogQueryDTO);
        return "index";
    }

    @GetMapping("/available-plants")
    public String availablePlants(CatalogQueryDTO query, Model model, HttpSession session){
        System.out.println("===> available-plants");
        System.out.println("===> query: "+query);


        try{
            List<PlantInventoryEntryDTO> entries = rentalService.findAvailablePlants(
                    query.getName(),
                    query.getRentalPeriod().getStartDate(),
                    query.getRentalPeriod().getEndDate());

            System.out.println("===> entries: "+entries);

            model.addAttribute("entries",entries);
            model.addAttribute("query",query);

            return "available_plants";
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }
        
        return index(model,session);
    }

    @GetMapping("/phr-list")
    public String getPhrList(Model model, HttpSession session) throws Exception {
        System.out.println("===> phr list");


        try{
            List<PlantHireRequestDTO> phrs = plantHiringService.getAllPlantHires();
            System.out.println("===> All phrs: "+phrs);

            model.addAttribute("phrs",phrs);
            return "plant_hire_request_list";
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        return index(model,session);
    }

    @GetMapping("/create-phr")
    public String createPhr(@RequestParam String plantLink,
                       @RequestParam String startDate,
                       @RequestParam String endDate,
                       @RequestParam String totalPrice,
                       Model model,
                       HttpSession session) {
        System.out.println("===> create-phr GET");
        ExtendedLink link = new ExtendedLink(plantLink, "self", HttpMethod.GET);

        try{
            PlantInventoryEntryDTO entry = rentalService.getPlant(link);

            PlantHireRequestDTO phr = new PlantHireRequestDTO();
            phr.setNameOfSiteEngineer("Chingiz");
            phr.setNameOfConstructionSite("Tartu-5");
            phr.setSupplier("RentIT");
            phr.setEntryId(entry.get_id());
            phr.setEntryName(entry.getName());
            phr.setStatus(PHRStatus.PENDING.toString());
            phr.setTotalCost(new BigDecimal(totalPrice));
            phr.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.parse(startDate),LocalDate.parse(endDate)));

            System.out.println("===> Created phr: "+phr);

            model.addAttribute("currentAction","create");
            model.addAttribute("phr",phr);
            return "phr";
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        return index(model,session); // Fallback
    }

    @PostMapping("/save-phr")
    public String savePhr(PlantHireRequestDTO phr, Model model, HttpSession session) throws Exception {
        System.out.println("===> Save phr");
        System.out.println("===> phr: "+phr);

        try{
            plantHiringService.createPlantHireRequest(phr);
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        List<PlantHireRequestDTO> phrs = plantHiringService.getAllPlantHires();
        System.out.println("===> All phrs: "+phrs);

        model.addAttribute("phrs",phrs);
        return "plant_hire_request_list";
    }

    @GetMapping("/edit-phr/{phrId}")
    public String editPhr(@PathVariable Long phrId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Edit phr");
        System.out.println("===> phrId: "+phrId);

        PlantHireRequestDTO phrDTO = plantHiringService.getPlantHireRequest(phrId);

        model.addAttribute("phr",phrDTO);
        model.addAttribute("currentAction","edit");
        return "phr";
    }

    @PostMapping("/update-phr")
    public String updatePhr(PlantHireRequestDTO phr, Model model, HttpSession session) throws Exception {
        System.out.println("===> Edit phr");
        System.out.println("===> phrDto: "+phr);

        try{
            System.out.println("===> will update phr");
            plantHiringService.updatePlantHireRequest(phr.getId(), phr);
            System.out.println("===> updated");
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        return getPhrList(model,session);
    }

    @GetMapping("/approve-phr/{phrId}")
    public String approvePhr(@PathVariable Long phrId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Approve phr");
        System.out.println("===> phrId: "+phrId);

        try{
            plantHiringService.approvePlantHireRequest(phrId,plantHiringService.getPlantHireRequest(phrId));
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        List<PlantHireRequestDTO> phrs = plantHiringService.getAllPlantHires();
        System.out.println("===> All phrDtos: "+phrs);

        model.addAttribute("phrs",phrs);
        return "plant_hire_request_list";
    }

    @GetMapping("/reject-phr/{phrId}")
    public String rejectPhr(@PathVariable Long phrId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Reject phr");
        System.out.println("===> phrId: "+phrId);

        try{
            plantHiringService.rejectPlantHireRequest(phrId,plantHiringService.getPlantHireRequest(phrId));
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        List<PlantHireRequestDTO> phrs = plantHiringService.getAllPlantHires();
        System.out.println("===> All phrDtos: "+phrs);

        model.addAttribute("phrs",phrs);
        return "plant_hire_request_list";
    }

    @GetMapping("/cancel-phr/{phrId}")
    public String cancelPhr(@PathVariable Long phrId, Model model, HttpSession session) throws Exception {
        System.out.println("===> Cancel phr");
        System.out.println("===> phrId: "+phrId);

        try{
            plantHiringService.cancelPlantHireRequest(phrId);
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        List<PlantHireRequestDTO> phrs = plantHiringService.getAllPlantHires();
        System.out.println("===> All phrDtos: "+phrs);

        model.addAttribute("phrs",phrs);
        return "plant_hire_request_list";
    }

    @GetMapping("/po-list")
    public String listPurchaseOrders(Model model, HttpSession session) throws Exception {
        System.out.println("===> List POs");

        try{
            List<PurchaseOrderDTO> pos = rentalService.getAllPurchaseOrders();
            List<Invoice> invoices = generateInvoiceListFromPoList(pos);
            System.out.println(invoices);
            String[] canBeExtended = {"REJECTED","CLOSED","CANCELLED","PLANT_RETURNED","PAID"};

            System.out.println("===> POs: "+pos);
            model.addAttribute("pos",pos);
            model.addAttribute("invoices",invoices);
            model.addAttribute("canBeExtended",canBeExtended);
            model.addAttribute("APPROVED_INVOICE", InvStatus.APPROVED);
            return "purchase_order_list";
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        return index(model,session);
    }

    @GetMapping("/approve-invoice/{invoiceId}")
    public String approveInvoice(@PathVariable Long invoiceId, Model model, HttpSession session) throws Exception {
        System.out.println("===> approve invoice");
        System.out.println("===> invoiceId: "+invoiceId);


        try{
            payablesService.approveInvoice(invoiceId);
        }catch(Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }

        return listPurchaseOrders(model,session);
    }

    @GetMapping("/extend-po/{poId}")
    public String extendPo(@PathVariable Long poId, Model model, HttpSession session) throws Exception {
        System.out.println("===> extend po");
        System.out.println("===> poId: "+poId);

        List<PurchaseOrderDTO> poList = rentalService.getAllPurchaseOrders();
        PurchaseOrderDTO po = findPOfromList(poId,poList);

        if(po == null){
            model.addAttribute("errorMessage","Purchase Order not found");
        }else{
            System.out.println("Found po: "+po);
            model.addAttribute("po",po);
            return "extend_po";
        }

        return listPurchaseOrders(model,session);
    }

    @PostMapping("/extend-po")
    public String extendPoPost(PurchaseOrderDTO po, Model model, HttpSession session) throws Exception {
        System.out.println("===> extend po post");
        System.out.println("===> po: "+po);

//        List<PurchaseOrderDTO> poList = rentalService.extendPurchaseOrder(po,);
//        PurchaseOrderDTO po = findPOfromList(poId,poList);
//
//        if(po == null){
//            model.addAttribute("errorMessage","Purchase Order not found");
//        }else{
//            model.addAttribute("po",po);
//            return "extend_po";
//        }

        return listPurchaseOrders(model,session);
    }

    private PurchaseOrderDTO findPOfromList(Long id, List<PurchaseOrderDTO> poList){
        for(PurchaseOrderDTO po : poList){
            if(po.getId() == id){
                return po;
            }
        }

        return null;
    }

    private List<Invoice> generateInvoiceListFromPoList(List<PurchaseOrderDTO> poList){
        List<Invoice> poInvoices = new ArrayList<>();
        List<Invoice> allInvoices = invoiceRepo.findAll();


        for(PurchaseOrderDTO po : poList){
            Invoice invoiceOfCurrentPo = null;

            for(Invoice invoice : allInvoices){
                if(invoice.getPo().getId() == po.getId()){
                    invoiceOfCurrentPo = invoice;
                }
            }

            poInvoices.add(invoiceOfCurrentPo);
        }

        return poInvoices;
    }
}
