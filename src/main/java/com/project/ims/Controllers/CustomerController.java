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
import com.project.ims.Models.Customer;
import com.project.ims.Requests.CustomerAddRequest;

@RestController
@RequestMapping("/api")
public class CustomerController {
    
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

    @GetMapping("/customer")
    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomer();

        return customers;
    }

    @GetMapping("/customer/{id}")
    public Customer getCustomerById(@PathVariable("id") String id) {
        Customer customer = customerService.getCustomerById(id);
        return customer;
    }

    @PostMapping("/customer")
    public Customer addCustomer(@RequestBody CustomerAddRequest data) {
        Customer customer = new Customer();
        Random rand = new Random();
        String id = 'c' + String.valueOf(rand.nextInt(1000000));
        customer.setId(id);
        customer.setName(data.getName());
        customer.setEmail(data.getEmail());
        customer.setPassword(data.getPassword());
        customer.setPhone(data.getPhone());
        customer.setAddress(data.getAddress());
        customer.setPincode(data.getPincode());
        return customer;
    }

    @PostMapping("/customer/{id}")
    public Customer updateCustomer(@PathVariable("id") String id, @RequestBody CustomerAddRequest data) {
        Customer customer = customerService.getCustomerById(id);
        customer.setName(data.getName());
        customer.setEmail(data.getEmail());
        customer.setPassword(data.getPassword());
        customer.setPhone(data.getPhone());
        customer.setAddress(data.getAddress());
        customer.setPincode(data.getPincode());
        return customer;
    }

    @DeleteMapping("/customer/{id}")
    public void deleteCustomer(@PathVariable("id") String id) {

        if(customerService.getCustomerById(id) == null)
            throw new RuntimeException("Customer with id " + id + " does not exist");

        customerService.deleteCustomer(id);
    }
}
