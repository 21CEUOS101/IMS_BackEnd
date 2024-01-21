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
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Requests.DeliveryManAddRequest;
import com.project.ims.Services.DeliveryManService;

@RestController
@RequestMapping("/api")
public class DeliveryManController {

    // necessary dependencies are injected here
    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // controllers

    // get all deliverymans
    @GetMapping("/deliveryman")
    public List<DeliveryMan> getAllDeliveryMans() {

        try{
            List<DeliveryMan> deliveryMans = deliveryManService.getAllDeliveryMan();
            return deliveryMans;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
        
    }

    // get deliveryman by id
    @GetMapping("/deliveryman/{id}")
    public DeliveryMan getDeliveryManById(@PathVariable("id") String id) {
        
        try{
            DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
            return deliveryMan;
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
        deliveryMan.setPhone(data.getPhone());
        deliveryMan.setWarehouseId(data.getWarehouseId());
        deliveryMan.setStatus(data.getStatus());


        try{
            deliveryManService.addDeliveryMan(deliveryMan);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return deliveryMan;
    }

    @PostMapping("/deliveryman/{id}")
    public DeliveryMan updateDeliveryMan(@PathVariable("id") String id, @RequestBody DeliveryManAddRequest data) {

        DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
        deliveryMan.setPhone(data.getPhone());
        deliveryMan.setWarehouseId(data.getWarehouseId());
        deliveryMan.setStatus(data.getStatus());

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
}
