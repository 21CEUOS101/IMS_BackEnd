package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.ims.IServices.IRSOService;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.RSORepo;
import com.project.ims.Repo.ReturnOrderRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.SupplyOrderRepo;
import com.project.ims.Repo.W2WOrderRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
public class RSOService implements IRSOService {
    
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

    @Autowired
    private W2WOrderRepo w2wOrderRepo;

    @Autowired
    private RSORepo returnSupplyOrderRepo;

    @Override
    public List<ReturnSupplyOrder> getAllReturnSupplyOrder() {
        return returnSupplyOrderRepo.findAll();
    }

    @Override
    public ReturnSupplyOrder getReturnSupplyOrderById(String id) {
        return returnSupplyOrderRepo.findById(id).orElse(null);
    }

    @Override
    public ReturnSupplyOrder addReturnSupplyOrder(ReturnSupplyOrder returnSupplyOrder) {

        if (returnSupplyOrder.getId() == null || returnSupplyOrder.getId().isEmpty())
        {
            throw new RuntimeException("Id is required");
        }
        else if (returnOrderRepo.existsById(returnSupplyOrder.getId()))
        {
            throw new RuntimeException("Return Supply Order with this id already exists");
        }
        else if(returnSupplyOrder.getId().startsWith("rso")) 
        {
            throw new RuntimeException("Return Supply Order id must start with 'rso'");
        }

        return returnSupplyOrderRepo.save(returnSupplyOrder);
    }

    @Override
    public ReturnSupplyOrder updateReturnSupplyOrder(ReturnSupplyOrder returnSupplyOrder) {
        return returnSupplyOrderRepo.save(returnSupplyOrder);
    }

    @Override
    public void deleteReturnSupplyOrder(String id) {
        if (!returnSupplyOrderRepo.existsById(id))
        {
            throw new RuntimeException("Return Supply Order with id " + id + " does not exist");
        }
        returnSupplyOrderRepo.deleteById(id);
    }
    
}
