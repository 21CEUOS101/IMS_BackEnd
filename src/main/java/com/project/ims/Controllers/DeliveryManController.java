package com.project.ims.Controllers;

import java.util.ArrayList;
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
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.User;
import com.project.ims.Requests.DeliveryManAddRequest;
import com.project.ims.Requests.DeliveryManUpdateRequest;
import com.project.ims.Responses.DeliveryManOutput;
import com.project.ims.Services.DeliveryManService;
import com.project.ims.Services.UserService;

@RestController
@RequestMapping("/api")
public class DeliveryManController {

    // necessary dependencies are injected here
    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    // controllers

    // get all deliverymans
    @GetMapping("/deliveryman")
    public List<DeliveryManOutput> getAllDeliveryMans() {

        List<DeliveryManOutput> output = new ArrayList<>();
        try{
            List<DeliveryMan> deliveryMans = deliveryManService.getAllDeliveryMan();

            // also get user details

            for(int i=0;i<deliveryMans.size();i++)
            {
                User user = userService.getUserByUserId(deliveryMans.get(i).getId());
                DeliveryManOutput deliveryManOutput = new DeliveryManOutput();
                deliveryManOutput.setId(deliveryMans.get(i).getId());
                deliveryManOutput.setName(user.getName());
                deliveryManOutput.setEmail(user.getEmail());
                deliveryManOutput.setPassword(user.getPassword());
                deliveryManOutput.setPhone(user.getPhone());
                deliveryManOutput.setWarehouseId(deliveryMans.get(i).getWarehouseId());
                deliveryManOutput.setStatus(deliveryMans.get(i).getStatus());
                output.add(deliveryManOutput);
            }
            return output;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
        
    }

    // get deliveryman by id
    @GetMapping("/deliveryman/{id}")
    public DeliveryManOutput getDeliveryManById(@PathVariable("id") String id) {
        
        try{
            DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);

            // also get user details
            User user = userService.getUserByUserId(deliveryMan.getId());
            DeliveryManOutput deliveryManOutput = new DeliveryManOutput();
            deliveryManOutput.setId(deliveryMan.getId());
            deliveryManOutput.setName(user.getName());
            deliveryManOutput.setEmail(user.getEmail());
            deliveryManOutput.setPassword(user.getPassword());
            deliveryManOutput.setPhone(user.getPhone());
            deliveryManOutput.setWarehouseId(deliveryMan.getWarehouseId());
            deliveryManOutput.setStatus(deliveryMan.getStatus());

            return deliveryManOutput;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    @PostMapping("/deliveryman")
    public DeliveryMan addDeliveryMan(@RequestBody DeliveryManAddRequest data) {

        String id = generateId();

        DeliveryMan deliveryMan = new DeliveryMan();
        deliveryMan.setId(id);
        deliveryMan.setWarehouseId(data.getWarehouseId());
        deliveryMan.setStatus("available");

        try{
            // creating user
            createUser(data.getName() , data.getEmail() , data.getPassword() , "deliveryman" , data.getPhone() , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        try{
            deliveryManService.addDeliveryMan(deliveryMan);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return deliveryMan;
    }

    @PostMapping("/deliveryman/{id}")
    public DeliveryMan updateDeliveryMan(@PathVariable("id") String id, @RequestBody DeliveryManUpdateRequest data) {

        DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
        deliveryMan.setWarehouseId(data.getWarehouseId());
        deliveryMan.setStatus(data.getStatus());

        try{
            // updating user
            updateUser(data.getName() , data.getEmail() , data.getPassword() , "deliveryman" , data.getPhone() , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        try {
            deliveryManService.updateDeliveryMan(deliveryMan);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return deliveryMan;
    }
    
    @DeleteMapping("/deliveryman/{id}")
    public void deleteDeliveryMan(@PathVariable("id") String id) {

        try{
            // deleting user
            deleteUser(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        try{
            deliveryManService.deleteDeliveryMan(id);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public String generateId() {
        // id generation
        Random rand = new Random();
        String id = 'd' + String.valueOf(rand.nextInt(1000000));

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
    
    public void updateUser(String name, String email, String password, String role, String phone, String userId) {
        User user = userService.getUserByUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
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
}
