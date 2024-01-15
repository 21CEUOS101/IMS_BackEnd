package com.project.ims.Services;

// imports
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.ISupplyOrderService;
import com.project.ims.Models.SupplyOrder;
import com.project.ims.Repo.ReturnOrderRepo;
import com.project.ims.Repo.SupplyOrderRepo;

@Component
@Service
public class SupplyOrderService implements ISupplyOrderService {

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

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

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

        if(supplyOrder == null)
        {
            throw new RuntimeException("Supply Order data shouldn't be null");
        }

        return supplyOrderRepo.save(supplyOrder);
    }

    @Override
    public void deleteSupplyOrder(String id) {

        if(id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }
        else if (!supplyOrderRepo.existsById(id))
        {
            throw new RuntimeException("Supply Order with id " + id + " does not exist");
        }

        supplyOrderRepo.deleteById(id);
    }
    
}
