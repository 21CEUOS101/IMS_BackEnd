package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.ims.IServices.IReturnOrderService;
import com.project.ims.Models.ReturnOrder;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.ReturnOrderRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
public class ReturnOrderService implements IReturnOrderService {
    
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

    @Override
    public List<ReturnOrder> getAllReturnOrder() {
        return returnOrderRepo.findAll();
    }

    @Override
    public ReturnOrder getReturnOrderById(String id) {
        return returnOrderRepo.findById(id).orElse(null);
    }

    @Override
    public ReturnOrder addReturnOrder(ReturnOrder returnOrder) {

        if (returnOrder.getId() == null || returnOrder.getId().isEmpty())
        {
            throw new RuntimeException("Return Order ID cannot be empty");
        }
        else if (returnOrderRepo.existsById(returnOrder.getId()))
        {
            throw new RuntimeException("Return Order ID already exists");
        }
        else if (returnOrder.getId().charAt(0) != 'r')
        {
            throw new RuntimeException("Return Order ID must start with 'r'");
        }

        return returnOrderRepo.save(returnOrder);
    }

    @Override
    public ReturnOrder updateReturnOrder(ReturnOrder returnOrder) {
        return returnOrderRepo.save(returnOrder);
    }

    @Override
    public void deleteReturnOrder(String id) {
        if (!returnOrderRepo.existsById(id))
        {
            throw new RuntimeException("Return Order ID does not exist");
        }
        returnOrderRepo.deleteById(id);
    }

    @Override
    public List<ReturnOrder> getAllReturnOrderByCustomerId(String id) {
        return returnOrderRepo.findAllByCustomerId(id);
    }
    
}
