package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.ims.IServices.IW2WOrderService;
import com.project.ims.Models.W2WOrder;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.ReturnOrderRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.SupplyOrderRepo;
import com.project.ims.Repo.W2WOrderRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
@Service
public class W2WOrderService implements IW2WOrderService {
    
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

    @Override
    public List<W2WOrder> getAllW2WOrder() {
        return w2wOrderRepo.findAll();
    }

    @Override
    public W2WOrder getW2WOrderById(String id) {
        return w2wOrderRepo.findById(id).orElse(null);
    }

    @Override
    public W2WOrder addW2WOrder(W2WOrder w2wOrder) {
        if(w2wOrder.getId() == null || w2wOrder.getId().isEmpty())
        {
            throw new RuntimeException("W2W Order ID cannot be empty");
        }
        else if (w2wOrderRepo.existsById(w2wOrder.getId()))
        {
            throw new RuntimeException("W2W Order ID already exists");
        }
        else if(w2wOrder.getId().charAt(0) != 'w')
        {
            throw new RuntimeException("W2W Order ID must start with 'w'");
        }

        return w2wOrderRepo.save(w2wOrder);
    }

    @Override
    public W2WOrder updateW2WOrder(W2WOrder w2wOrder) {
        return w2wOrderRepo.save(w2wOrder);
    }

    @Override
    public void deleteW2WOrder(String id) {
        if (!w2wOrderRepo.existsById(id))
        {
            throw new RuntimeException("W2W Order with id " + id + " does not exist");
        }
        
        w2wOrderRepo.deleteById(id);
    }
    
}
