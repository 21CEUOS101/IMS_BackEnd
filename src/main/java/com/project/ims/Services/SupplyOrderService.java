package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.ims.IServices.ISupplyOrderService;
import com.project.ims.Models.SupplyOrder;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.ReturnOrderRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.SupplyOrderRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
@Service
public class SupplyOrderService implements ISupplyOrderService {

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

    @Autowired
    private ReturnOrderRepo returnOrderRepo;

    @Autowired
    private SupplyOrderRepo supplyOrderRepo;

    @Override
    public List<SupplyOrder> getAllSupplyOrder() {
        return supplyOrderRepo.findAll();
    }

    @Override
    public SupplyOrder getSupplyOrderById(String id) {
        return supplyOrderRepo.findById(id).orElse(null);
    }

    @Override
    public SupplyOrder addSupplyOrder(SupplyOrder supplyOrder) {

        if(supplyOrder.getId() == null || supplyOrder.getId().isEmpty())
        {
            throw new RuntimeException("Supply Order ID cannot be empty");
        }
        else if(supplyOrderRepo.existsById(supplyOrder.getId()))
        {
            throw new RuntimeException("Supply Order ID already exists");
        }
        else if (supplyOrder.getId().charAt(0) != 's')
        {
            throw new RuntimeException("Supply Order ID must start with 's'");
        }
        return supplyOrderRepo.save(supplyOrder);
    }

    @Override
    public SupplyOrder updateSupplyOrder(SupplyOrder supplyOrder) {
        return supplyOrderRepo.save(supplyOrder);
    }

    @Override
    public void deleteSupplyOrder(String id) {
        if (!supplyOrderRepo.existsById(id))
        {
            throw new RuntimeException("Supply Order with id " + id + " does not exist");
        }
        returnOrderRepo.deleteById(id);
    }
    
}
