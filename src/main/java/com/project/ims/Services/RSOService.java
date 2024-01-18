package com.project.ims.Services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
// imports
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IRSOService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Repo.RSORepo;
import com.project.ims.Repo.ReturnOrderRepo;

@Service
public class RSOService implements IRSOService {

    // necessary dependency Injections
    @Autowired
    private ReturnOrderRepo returnOrderRepo;

    @Autowired
    private RSORepo returnSupplyOrderRepo;

    @Autowired
    private DeliveryManService deliveryManService;

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

        // set date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        returnSupplyOrder.setDate_time(formattedDateTime);
        
        // assign deliveryman to return supply order if deliveryman not available then rso status will be pending
        String deliveryManId = assignDeliveryMan(returnSupplyOrder);

        if (deliveryManId == null) {
            returnSupplyOrder.setStatus("pending");
        }

        returnSupplyOrder.setDelivery_man_id(deliveryManId);
        

        return returnSupplyOrderRepo.save(returnSupplyOrder);
    }

    @Override
    public ReturnSupplyOrder updateReturnSupplyOrder(ReturnSupplyOrder returnSupplyOrder) {

        if (returnSupplyOrder == null) {
            throw new RuntimeException("Return Supply Order data shouldn't be null");
        }

        return returnSupplyOrderRepo.save(returnSupplyOrder);
    }
    
    @Override
    public ReturnSupplyOrder updateReturnSupplyOrderStatus(String id, String status) {
        ReturnSupplyOrder returnSupplyOrder = getReturnSupplyOrderById(id);

        if(status.equals("delivered")) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            returnSupplyOrder.setDelivered_date_time(formattedDateTime);

            DeliveryMan m = deliveryManService.getDeliveryManById(returnSupplyOrder.getDelivery_man_id());

            m.setStatus("available");

            try{
                deliveryManService.updateDeliveryMan(m);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }
        }

        returnSupplyOrder.setStatus(status);
        return returnSupplyOrderRepo.save(returnSupplyOrder);
    }

    @Override
    public void deleteReturnSupplyOrder(String id) {

        if (id == null) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!returnSupplyOrderRepo.existsById(id)) {
            throw new RuntimeException("Return Supply Order with id " + id + " does not exist");
        }
        returnSupplyOrderRepo.deleteById(id);
    }
    
    public String assignDeliveryMan(ReturnSupplyOrder returnSupplyOrder) {
        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(returnSupplyOrder.getWarehouse_id());
        String deliveryManId = null;
        for (DeliveryMan deliveryMan : deliveryMen) {
            if (deliveryMan.getStatus().equals("available")) {
                deliveryManId = deliveryMan.getId();
                deliveryMan.setStatus("unavailable");
                try{
                    deliveryManService.updateDeliveryMan(deliveryMan);
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                    return null;
                }
                break;
            }
        }
        return deliveryManId;
    }
    
}
