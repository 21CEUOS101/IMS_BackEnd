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
import com.project.ims.Models.Customer;
import com.project.ims.Models.User;
import com.project.ims.Requests.CustomerAddRequest;
import com.project.ims.Services.CustomerService;
import com.project.ims.Services.UserService;

@RestController
@RequestMapping("/api")
public class CustomerController {

    // necessary dependency injections
    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    // controllers

    // get all customers
    @GetMapping("/customer")
    public List<Customer> getAllCustomers() {
        try{
            List<Customer> customers = customerService.getAllCustomer();
            return customers;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // get customer by id
    @GetMapping("/customer/{id}")
    public Customer getCustomerById(@PathVariable("id") String id) {
        try{
            Customer customer = customerService.getCustomerById(id);
            return customer;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping("/customer")
    public Customer addCustomer(@RequestBody CustomerAddRequest data) {

        String id = generateId();

        Customer customer = new Customer();
        customer.setId(id);
        customer.setPhone(data.getPhone());
        customer.setAddress(data.getAddress());
        customer.setPincode(data.getPincode());

        try{
            // creating user
            createUser(data.getName() , data.getEmail() , data.getPassword() , "customer" , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        try{
            customerService.addCustomer(customer);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return customer;
    }

    @PostMapping("/customer/{id}")
    public Customer updateCustomer(@PathVariable("id") String id, @RequestBody CustomerAddRequest data) {

        Customer customer = customerService.getCustomerById(id);
        customer.setPhone(data.getPhone());
        customer.setAddress(data.getAddress());
        customer.setPincode(data.getPincode());

        try{
            // updating user
            updateUser(data.getName() , data.getEmail() , data.getPassword() , "customer" , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        try {
            customerService.updateCustomer(customer);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return customer;
    }

    @DeleteMapping("/customer/{id}")
    public void deleteCustomer(@PathVariable("id") String id) {

        try{
            // deleting user
            deleteUser(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        try{
            customerService.deleteCustomer(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    public String generateId() {
        // id generation
        Random rand = new Random();
        String id = 'c' + String.valueOf(rand.nextInt(1000000));

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
