package com.project.ims.Controllers;

// imports
import java.util.List;
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
import com.project.ims.Models.Supplier;
import com.project.ims.Models.User;
import com.project.ims.Requests.SupplierAddRequest;
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
    public List<Supplier> getAllSuppliers() {
        try{
            List<Supplier> suppliers = supplierService.getAllSupplier();
            return suppliers;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // get supplier by id

    @GetMapping("/supplier/{id}")
    public Supplier getSupplierById(@PathVariable("id") String id) {
        try{
            Supplier supplier = supplierService.getSupplierById(id);
            return supplier;
        }
        catch(Exception e)
        {
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
        supplier.setPhone(data.getPhone());
        supplier.setPincode(data.getPincode());

        try{
            // creating user
            createUser(data.getName() , data.getEmail() , data.getPassword() , "supplier" , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
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
    public Supplier updateSupplier(@PathVariable("id") String id, @RequestBody SupplierAddRequest data) {

        Supplier supplier = supplierService.getSupplierById(id);
        supplier.setAddress(data.getAddress());
        supplier.setPhone(data.getPhone());
        supplier.setPincode(data.getPincode());

        try{
            // updating user
            updateUser(data.getName() , data.getEmail() , data.getPassword() , "supplier" , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
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

        try{
            // deleting user
            deleteUser(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
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

    public void createUser(String name, String email, String password, String role, String userId) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setUserId(userId);

        try {
            userService.addUser(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void updateUser(String name, String email, String password, String role, String userId) {
        User user = userService.getUserByUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setUserId(userId);

        try {
            userService.updateUser(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteUser(String userId) {
        try {
            userService.deleteUserByUserId(userId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
