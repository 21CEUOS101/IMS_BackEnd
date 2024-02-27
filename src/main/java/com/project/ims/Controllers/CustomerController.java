package com.project.ims.Controllers;

// imports 
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
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
import com.project.ims.Models.Order;
import com.project.ims.Models.User;
import com.project.ims.Requests.Customer.CustomerAddRequest;
import com.project.ims.Requests.Customer.CustomerUpdateRequest;
import com.project.ims.Responses.CustomerOutput;
import com.project.ims.Services.CustomerService;
import com.project.ims.Services.OrderService;
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

    @Autowired
    private OrderService orderService;

    // controllers

    // get all customers
    @GetMapping("/customer")
    public List<CustomerOutput> getAllCustomers() {
        try {
            List<CustomerOutput> output = new ArrayList<>();
            List<Customer> customers = customerService.getAllCustomer();

            // also get user details
            for(int i=0;i<customers.size();i++)
            {
                User user = userService.getUserByUserId(customers.get(i).getId());
                CustomerOutput customerOutput = new CustomerOutput();
                customerOutput.setId(customers.get(i).getId());
                customerOutput.setName(user.getName());
                customerOutput.setEmail(user.getEmail());
                customerOutput.setPassword(user.getPassword());
                customerOutput.setPhone(user.getPhone());
                customerOutput.setAddress(customers.get(i).getAddress());
                customerOutput.setPincode(customers.get(i).getPincode());
                output.add(customerOutput);
            }

            return output;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // get customer by id
    @GetMapping("/customer/{id}")
    public CustomerOutput getCustomerById(@PathVariable("id") String id) {
        try {
            Customer customer = customerService.getCustomerById(id);

            // also get user details
            User user = userService.getUserByUserId(customer.getId());
            CustomerOutput customerOutput = new CustomerOutput();
            customerOutput.setId(customer.getId());
            customerOutput.setName(user.getName());
            customerOutput.setEmail(user.getEmail());
            customerOutput.setPassword(user.getPassword());
            customerOutput.setPhone(user.getPhone());
            customerOutput.setAddress(customer.getAddress());
            customerOutput.setPincode(customer.getPincode());

            return customerOutput;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping("/customer")
    public Customer addCustomer(@RequestBody CustomerAddRequest data) {

        String id = generateId();

        Customer customer = new Customer();
        customer.setId(id);
        customer.setAddress(data.getAddress());
        customer.setPincode(data.getPincode());

        try{
            // creating user
            createUser(data.getName() , data.getEmail() , data.getPassword() , "customer" , data.getPhone() , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
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
    public Customer updateCustomer(@PathVariable("id") String id, @RequestBody CustomerUpdateRequest data) {

        Customer customer = customerService.getCustomerById(id);
        customer.setAddress(data.getAddress());
        customer.setPincode(data.getPincode());

        try{
            // updating user
            updateUser(data.getName() , data.getEmail() , "customer" , data.getPhone() , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
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

    public void createUser(String name, String email, String password, String role, String phone, String userId) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);
        user.setUserId(userId);

        try {
            userService.addUser(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void updateUser(String name, String email, String role, String phone, String userId) {
        User user = userService.getUserByUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
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

    // get all orders of a customer
    @GetMapping("/customer/{id}/orders")
    public List<Order> getCustomerOrders(@PathVariable("id") String id) {
        try {
            return orderService.getAllOrderByCustomerId(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
