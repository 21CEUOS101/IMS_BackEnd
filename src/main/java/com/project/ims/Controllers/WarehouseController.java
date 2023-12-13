package com.project.ims.Controllers;

import org.springframework.web.bind.annotation.RestController;

import com.project.ims.IServices.IAdminService;
import com.project.ims.IServices.ICustomerService;
import com.project.ims.IServices.IDeliveryManService;
import com.project.ims.IServices.IOrderService;
import com.project.ims.IServices.IProductService;
import com.project.ims.IServices.ISupplierService;
import com.project.ims.IServices.IWManagerService;
import com.project.ims.IServices.IWareHouseService;
import com.project.ims.Models.WareHouse;
import com.project.ims.Requests.WareHouseAddRequest;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class WarehouseController {

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

    @GetMapping("/warehouse/{id}")
    public WareHouse getMethodName(@PathVariable String id) {
        return wareHouseService.getWareHouseById(id);
    }

    @GetMapping("/warehouse")
    public List<WareHouse> getAllWareHouses() {
        return wareHouseService.getAllWareHouse();
    }

    @PostMapping("/warehouse")
    public WareHouse createWarehouse(@RequestBody WareHouseAddRequest data) {

        WareHouse wareHouse = new WareHouse();
        Random rand = new Random();
        String id = 'w' + String.valueOf(rand.nextInt(1000000));
        wareHouse.setId(id);
        wareHouse.setName(data.getName());
        wareHouse.setAddress(data.getAddress());
        wareHouse.setPincode(data.getPincode());
        wareHouse.setManager_id(data.getManager_id());
        wareHouse.setStatus(data.getStatus());
        wareHouse.setProducts(data.getProducts());
        return wareHouseService.addWareHouse(wareHouse);

    }
    
    @PostMapping("/warehouse/{id}")
    public WareHouse updateWareHouse(@PathVariable String id,@RequestBody WareHouseAddRequest data) {

        WareHouse wareHouse = wareHouseService.getWareHouseById(id);
        wareHouse.setName(data.getName());
        wareHouse.setAddress(data.getAddress());
        wareHouse.setPincode(data.getPincode());
        wareHouse.setManager_id(data.getManager_id());
        wareHouse.setStatus(data.getStatus());
        wareHouse.setProducts(data.getProducts());
        return wareHouseService.addWareHouse(wareHouse);
    }
    
    @DeleteMapping("/warehouse/{id}")
    public void deleteWareHouse(@PathVariable String id) {
        wareHouseService.deleteWareHouse(id);
    }
}
