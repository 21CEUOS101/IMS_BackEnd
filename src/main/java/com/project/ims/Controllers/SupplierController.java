package com.project.ims.Controllers;

import java.util.ArrayList;
// imports
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Models.Supplier;
import com.project.ims.Models.SupplyOrder;
import com.project.ims.Models.User;
import com.project.ims.Requests.Supplier.SupplierAddRequest;
import com.project.ims.Requests.Supplier.SupplierUpdateRequest;
import com.project.ims.Responses.SupplierOutput;
import com.project.ims.Services.SupplierService;
import com.project.ims.Services.UserService;

@RestController
@RequestMapping("/api")
public class SupplierController {

    // necessary dependency injections
    @Autowired
    private SupplierService supplierService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    // Suppliers API

    // get all suppliers

    @GetMapping("/supplier")
    public List<SupplierOutput> getAllSuppliers() {
        try {
            List<SupplierOutput> output = new ArrayList<>();
            List<Supplier> suppliers = supplierService.getAllSupplier();

            // also get user details
            for(int i=0;i<suppliers.size();i++)
            {
                User user = userService.getUserByUserId(suppliers.get(i).getId());
                SupplierOutput supplierOutput = new SupplierOutput();
                supplierOutput.setId(suppliers.get(i).getId());
                supplierOutput.setName(user.getName());
                supplierOutput.setEmail(user.getEmail());
                supplierOutput.setPassword(user.getPassword());
                supplierOutput.setPhone(user.getPhone());
                supplierOutput.setAddress(suppliers.get(i).getAddress());
                supplierOutput.setPincode(suppliers.get(i).getPincode());
                output.add(supplierOutput);
            }

            return output;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // get supplier by id

    @GetMapping("/supplier/{id}")
    public SupplierOutput getSupplierById(@PathVariable("id") String id) {
        try {

            Supplier supplier = supplierService.getSupplierById(id);

            // also get user details
            User user = userService.getUserByUserId(supplier.getId());
            SupplierOutput supplierOutput = new SupplierOutput();
            supplierOutput.setId(supplier.getId());
            supplierOutput.setName(user.getName());
            supplierOutput.setEmail(user.getEmail());
            supplierOutput.setPassword(user.getPassword());
            supplierOutput.setPhone(user.getPhone());
            supplierOutput.setAddress(supplier.getAddress());
            supplierOutput.setPincode(supplier.getPincode());

            return supplierOutput;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // add supplier

    @PostMapping("/supplier")
    public Supplier addSupplier(@RequestBody SupplierAddRequest data) {

        String id = generateId();

        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setAddress(data.getAddress());
        supplier.setPincode(data.getPincode());

        try{
            // creating user
            createUser(data.getName() , data.getEmail() , data.getPassword() , "supplier" , data.getPhone() , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
        
        try{
            supplierService.addSupplier(supplier);
            return supplier;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    // update supplier

    @PostMapping("/supplier/{id}")
    public Supplier updateSupplier(@PathVariable("id") String id, @RequestBody SupplierUpdateRequest data) {

        Supplier supplier = supplierService.getSupplierById(id);
        supplier.setAddress(data.getAddress());
        supplier.setPincode(data.getPincode());

        try{
            // updating user
            updateUser(data.getName() , data.getEmail() , "supplier" , data.getPhone() , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        try {
            supplierService.updateSupplier(supplier);
            return supplier;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // delete supplier

    @DeleteMapping("/supplier/{id}")
    public void deleteSupplier(@PathVariable("id") String id) {

        User user = userService.getUserByUserId(id);
        try{
            // deleting user
            deleteUser(user.getUserId());
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return;
        }

        try{
            supplierService.deleteSupplier(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public String generateId() {
        // id generation
        Random rand = new Random();
        String id = 's' + String.valueOf(rand.nextInt(1000000));

        return id;
    }

    public void createUser(String name, String email, String password, String role, String phone, String userId) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);
        user.setUserId(userId);
        userService.addUser(user);
    }
   
    public void updateUser(String name, String email, String role, String phone, String userId) {
        User user = userService.getUserByUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setUserId(userId);
        userService.updateUser(user);
    }

    public void deleteUser(String userId) {
        userService.deleteUserByUserId(userId);
    }
    
    // get all supply orders by supplier
    @GetMapping("/supplier/{id}/supply-orders")
    public List<SupplyOrder> getSupplyOrdersBySupplier(@PathVariable String id) {
        try {
            List<SupplyOrder> orders = supplierService.getSupplyOrdersBySupplier(id);
            return orders;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // get all return supply orders by supplier
    @GetMapping("/supplier/{id}/return-supply-orders")
    public List<ReturnSupplyOrder> getReturnSupplyOrdersBySupplier(@PathVariable String id) {
        try {
            List<ReturnSupplyOrder> orders = supplierService.getReturnSupplyOrdersBySupplier(id);
            return orders;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
