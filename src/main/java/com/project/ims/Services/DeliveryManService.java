package com.project.ims.Services;

import java.util.List;

// imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IDeliveryManService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
@Service
public class DeliveryManService implements IDeliveryManService {

    // necessary dependency Injections
    @Autowired
    private DeliveryManRepo deliveryManRepo;

    @Autowired
    private WareHouseRepo wareHouseRepo;

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
    
}
