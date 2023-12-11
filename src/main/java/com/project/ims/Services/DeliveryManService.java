package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.ims.IServices.IDeliveryManService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
public class DeliveryManService implements IDeliveryManService {
    
    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private DeliveryManRepo deliveryManRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private SupplierRepo supplierRepo;

    @Autowired
    private WareHouseRepo wareHouseRepo;
    
    @Autowired
    private WManagerRepo wManagerRepo;

    @Override
    public DeliveryMan getDeliveryManById(String id) {
        return deliveryManRepo.findById(id).orElse(null);
    }

    @Override
    public DeliveryMan addDeliveryMan(DeliveryMan deliveryMan) {
        
        if(deliveryManRepo.existsById(deliveryMan.getId()))
            throw new RuntimeException("DeliveryMan with id " + deliveryMan.getId() + " already exists");
        else if (deliveryMan.getId() == null || deliveryMan.getId().isEmpty())
            throw new RuntimeException("DeliveryMan ID cannot be empty");
        else if (deliveryMan.getId().charAt(0) != 'd')
            throw new RuntimeException("DeliveryMan ID must start with 'd'");
        
        return deliveryManRepo.save(deliveryMan);
    }

    @Override
    public DeliveryMan updateDeliveryMan(DeliveryMan deliveryMan) {
        return deliveryManRepo.save(deliveryMan);
    }

    @Override
    public void deleteDeliveryMan(String id) {
        
        if(!deliveryManRepo.existsById(id))
            throw new RuntimeException("DeliveryMan with id " + id + " does not exist");
        
        deliveryManRepo.deleteById(id);
    }

    @Override
    public List<DeliveryMan> getAllDeliveryMan() {
        return deliveryManRepo.findAll();
    }
    
}
