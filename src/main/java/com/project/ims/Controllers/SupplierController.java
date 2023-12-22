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
import com.project.ims.Models.Supplier;
import com.project.ims.Requests.SupplierAddRequest;
import com.project.ims.Services.SupplierService;
import com.project.ims.Services.WareHouseService;

@RestController
@RequestMapping("/api")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // Suppliers API

    // get all suppliers

    @GetMapping("/suppliers")
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSupplier();
    }

    // get supplier by id

    @GetMapping("/supplier/{id}")
    public Supplier getSupplierById(@PathVariable("id") String id) {
        return supplierService.getSupplierById(id);
    }

    // add supplier

    @PostMapping("/supplier")
    public Supplier addSupplier(@RequestBody SupplierAddRequest data) {

        Supplier supplier = new Supplier();
        Random rand = new Random();
        int id = rand.nextInt(1000000);
        supplier.setId('s' + Integer.toString(id));
        supplier.setName(data.getName());
        supplier.setAddress(data.getAddress());
        supplier.setEmail(data.getEmail());
        supplier.setPassword(data.getPassword());
        supplier.setPhone(data.getPhone());
        supplier.setPincode(data.getPincode());
        return supplierService.addSupplier(supplier);
    }
    
    // update supplier

    @PostMapping("/supplier/{id}")
    public Supplier updateSupplier(@PathVariable("id") String id, @RequestBody SupplierAddRequest data) {

        Supplier supplier = supplierService.getSupplierById(id);
        supplier.setName(data.getName());
        supplier.setAddress(data.getAddress());
        supplier.setEmail(data.getEmail());
        supplier.setPassword(data.getPassword());
        supplier.setPhone(data.getPhone());
        supplier.setPincode(data.getPincode());
        return supplierService.updateSupplier(supplier);
    }

    // delete supplier

    @DeleteMapping("/supplier/{id}")
    public void deleteSupplier(@PathVariable("id") String id) {
        supplierService.deleteSupplier(id);
    }
}
