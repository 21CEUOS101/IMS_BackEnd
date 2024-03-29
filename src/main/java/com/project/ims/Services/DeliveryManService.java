package com.project.ims.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IDeliveryManService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.User;
import com.project.ims.Models.WareHouse;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.WareHouseRepo;

@Service
public class DeliveryManService implements IDeliveryManService {

    // necessary dependency Injections
    @Autowired
    private DeliveryManRepo deliveryManRepo;

    @Autowired
    private WareHouseRepo wareHouseRepo;
    @Autowired
    private WareHouseService wareHouseService;
    @Autowired
    private UserService userService;
    // Services

    // get deliveryman by id
    @Override
    public DeliveryMan getDeliveryManById(String id) {

        if (id == null)
            throw new RuntimeException("Id shouldn't be null");

        return deliveryManRepo.findById(id).orElse(null);
    }

    @Override
    public DeliveryMan addDeliveryMan(DeliveryMan deliveryMan) {
        
        if(deliveryManRepo.existsById(deliveryMan.getId()))
        {
            throw new RuntimeException("DeliveryMan with id " + deliveryMan.getId() + " already exists");
        }
        else if (deliveryMan.getId() == null || deliveryMan.getId().isEmpty())
        {
            throw new RuntimeException("DeliveryMan ID cannot be empty");
        }
        else if (deliveryMan.getId().charAt(0) != 'd')
        {
            throw new RuntimeException("DeliveryMan ID must start with 'd'");
        }
        else if (deliveryMan.getWarehouseId() == null || deliveryMan.getWarehouseId().isEmpty())
        {
            throw new RuntimeException("Warehouse ID cannot be empty");
        }
        else if (!wareHouseRepo.existsById(deliveryMan.getWarehouseId()))
        {
            throw new RuntimeException("Warehouse with id " + deliveryMan.getWarehouseId() + " does not exist");
        }
        
        return deliveryManRepo.save(deliveryMan);
    }

    @Override
    public DeliveryMan updateDeliveryMan(DeliveryMan deliveryMan) {

        // checking if deliveryman data is null or not
        if (deliveryMan == null)
        {
            throw new RuntimeException("DeliveryMan data shouldn't be null");
        }

        return deliveryManRepo.save(deliveryMan);
    }

    @Override
    public void deleteDeliveryMan(String id) {

        if(id == null || id.isEmpty())
        {
            throw new RuntimeException("Id cannot be empty");
        }
        
        if(!deliveryManRepo.existsById(id))
        {
            throw new RuntimeException("DeliveryMan with id " + id + " does not exist");
        }
        
        deliveryManRepo.deleteById(id);
    }

    @Override
    public List<DeliveryMan> getAllDeliveryMan() {
        return deliveryManRepo.findAll();
    }

    @Override
    public List<DeliveryMan> getAllDeliveryManByWarehouse(String wareHouseId) {

        if(wareHouseId == null || wareHouseId.isEmpty())
        {
            throw new RuntimeException("WareHouseId cannot be empty");
        }
        
        if(!wareHouseRepo.existsById(wareHouseId))
        {
            throw new RuntimeException("WareHouse with id " + wareHouseId + " does not exist");
        }
        
        return deliveryManRepo.findByWarehouseId(wareHouseId);
    }

    //custom

    public  Map<String, Object> Dprofile(String id){

        if (id.equals("")) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!deliveryManRepo.existsById(id)) {
            throw new RuntimeException("DeliveryMan  with id " + id + " does not exist");
        }
        DeliveryMan deliveryMan = getDeliveryManById(id);
        if(deliveryMan == null)
        {
            System.out.println("Delivery man not exists");
            return null;
        }
        WareHouse wareHouse = wareHouseService.getWareHouseById( deliveryMan.getWarehouseId());
        if(wareHouse == null)
        {
            System.out.println("delivery man warehouse donot exists");
            return null;
        }
        // System.out.println(deliveryMan);
        User user = userService.getUserByUserId(id);
        System.out.println("hello2");
        Map<String, Object> profile = new HashMap<>();
        profile.put("deliveryman", deliveryMan);
        profile.put("warehouse",wareHouse);
        profile.put("user", user);
        return profile;
    }
    
}
