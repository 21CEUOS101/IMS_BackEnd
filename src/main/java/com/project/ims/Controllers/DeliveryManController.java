package com.project.ims.Controllers;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.ims.IServices.IAdminService;
import com.project.ims.IServices.ICustomerService;
import com.project.ims.IServices.IDeliveryManService;
import com.project.ims.IServices.IOrderService;
import com.project.ims.IServices.IProductService;
import com.project.ims.IServices.ISupplierService;
import com.project.ims.IServices.IWManagerService;
import com.project.ims.IServices.IWareHouseService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Requests.DeliveryManAddRequest;

@RestController
@RequestMapping("/api")
public class DeliveryManController {
    
    @Autowired
    private IAdminService adminService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IDeliveryManService deliveryManService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IProductService productService;

    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private IWareHouseService wareHouseService;

    @Autowired
    private IWManagerService wManagerService;

    @GetMapping("/deliveryman")
    public List<DeliveryMan> getAllDeliveryMans() {
        List<DeliveryMan> deliveryMans = deliveryManService.getAllDeliveryMan();
        return deliveryMans;
    }

    @GetMapping("/deliveryman/{id}")
    public DeliveryMan getDeliveryManById(@PathVariable("id") String id) {
        DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
        return deliveryMan;
    }

    @PostMapping("/deliveryman")
    public DeliveryMan addDeliveryMan(@RequestBody DeliveryManAddRequest data) {

        DeliveryMan deliveryMan = new DeliveryMan();
        Random rand = new Random();
        String id = 'd' + String.valueOf(rand.nextInt(1000000));
        deliveryMan.setId(id);
        deliveryMan.setName(data.getName());
        deliveryMan.setEmail(data.getEmail());
        deliveryMan.setPassword(data.getPassword());
        deliveryMan.setPhone(data.getPhone());
        deliveryMan.setWarehouse_id(data.getWarehouse_id());
        deliveryMan.setStatus(data.getStatus());
        deliveryManService.addDeliveryMan(deliveryMan);
        return deliveryMan;
    }

    @PostMapping("/deliveryman/{id}")
    public DeliveryMan updateDeliveryMan(@PathVariable("id") String id, @RequestBody DeliveryManAddRequest data) {
        DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
        deliveryMan.setName(data.getName());
        deliveryMan.setEmail(data.getEmail());
        deliveryMan.setPassword(data.getPassword());
        deliveryMan.setPhone(data.getPhone());
        deliveryMan.setWarehouse_id(data.getWarehouse_id());
        deliveryMan.setStatus(data.getStatus());
        deliveryManService.updateDeliveryMan(deliveryMan);
        return deliveryMan;
    }
    
    @DeleteMapping("/deliveryman/{id}")
    public void deleteDeliveryMan(@PathVariable("id") String id) {
        deliveryManService.deleteDeliveryMan(id);
    }
}
