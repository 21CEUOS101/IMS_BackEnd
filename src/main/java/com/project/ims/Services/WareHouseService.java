package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.ims.IServices.IWareHouseService;
import com.project.ims.Models.WareHouse;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
public class WareHouseService implements IWareHouseService {
    
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
    public WareHouse getWareHouseById(String id) {
        return wareHouseRepo.findById(id).orElse(null);
    }

    @Override
    public WareHouse addWareHouse(WareHouse wareHouse) {

        if (wareHouse.getId() == null || wareHouse.getId().isEmpty())
        {
            throw new RuntimeException("WareHouse ID cannot be empty");
        }
        else if (wareHouseRepo.existsById(wareHouse.getId()))
        {
            throw new RuntimeException("WareHouse ID already exists");
        }
        else if (wareHouse.getId().charAt(0) != 'w')
        {
            throw new RuntimeException("WareHouse ID must start with 'w'");
        }

        return wareHouseRepo.save(wareHouse);
    }

    @Override
    public WareHouse updateWareHouse(WareHouse wareHouse) {
        return wareHouseRepo.save(wareHouse);
    }

    @Override
    public void deleteWareHouse(String id) {
        if (!wareHouseRepo.existsById(id))
        {
            throw new RuntimeException("WareHouse ID does not exist");
        }
        wareHouseRepo.deleteById(id);
    }

    @Override
    public List<WareHouse> getAllWareHouse() {
        return wareHouseRepo.findAll();
    }
    
}
