package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.ims.IServices.IWManagerService;
import com.project.ims.Models.WareHouse_Manager;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
public class WManagerService implements IWManagerService {
    
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
    public WareHouse_Manager getWManagerById(String id) {
        return wManagerRepo.findById(id).orElse(null);
    }

    @Override
    public WareHouse_Manager addWManager(WareHouse_Manager wManager) {

        if (wManager.getId() == null || wManager.getId().isEmpty())
        {
            throw new RuntimeException("WareHouse Manager ID cannot be empty");
        }
        else if (wManagerRepo.existsById(wManager.getId()))
        {
            throw new RuntimeException("WareHouse Manager ID already exists");
        }
        else if (wManager.getId().charAt(0) != 'm')
        {
            throw new RuntimeException("WareHouse Manager ID must start with 'm'");
        }

        return wManagerRepo.save(wManager);
    }

    @Override
    public WareHouse_Manager updateWManager(WareHouse_Manager wManager) {
        return wManagerRepo.save(wManager);
    }

    @Override
    public void deleteWManager(String id) {
        if (!wManagerRepo.existsById(id))
        {
            throw new RuntimeException("WareHouse Manager ID does not exist");
        }
        wManagerRepo.deleteById(id);
    }

    @Override
    public List<WareHouse_Manager> getAllWManager() {
        return wManagerRepo.findAll();
    }
    
}
