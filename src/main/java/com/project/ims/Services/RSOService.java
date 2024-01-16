package com.project.ims.Services;

// imports
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IRSOService;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Repo.RSORepo;
import com.project.ims.Repo.ReturnOrderRepo;

@Component
@Service
public class RSOService implements IRSOService {

    // necessary dependency Injections
    @Autowired
    private ReturnOrderRepo returnOrderRepo;

    @Autowired
    private RSORepo returnSupplyOrderRepo;

    // Services

    @Override
    public List<ReturnSupplyOrder> getAllReturnSupplyOrder() {
        return returnSupplyOrderRepo.findAll();
    }

    @Override
    public ReturnSupplyOrder getReturnSupplyOrderById(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

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
        else if(!returnSupplyOrder.getId().startsWith("rso")) 
        {
            throw new RuntimeException("Return Supply Order id must start with 'rso'");
        }

        return returnSupplyOrderRepo.save(returnSupplyOrder);
    }

    @Override
    public ReturnSupplyOrder updateReturnSupplyOrder(ReturnSupplyOrder returnSupplyOrder) {

        if (returnSupplyOrder == null)
        {
            throw new RuntimeException("Return Supply Order data shouldn't be null");
        }

        return returnSupplyOrderRepo.save(returnSupplyOrder);
    }

    @Override
    public void deleteReturnSupplyOrder(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }
        else if (!returnSupplyOrderRepo.existsById(id))
        {
            throw new RuntimeException("Return Supply Order with id " + id + " does not exist");
        }
        returnSupplyOrderRepo.deleteById(id);
    }
    
}
