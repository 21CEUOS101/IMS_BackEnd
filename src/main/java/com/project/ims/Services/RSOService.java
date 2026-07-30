package com.project.ims.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
// imports
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IRSOService;
import com.project.ims.Models.Customer;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.Product;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Models.Supplier;
import com.project.ims.Models.SupplyOrder;
import com.project.ims.Models.User;
import com.project.ims.Models.WareHouse;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.RSORepo;
import com.project.ims.Repo.ReturnOrderRepo;
import com.project.ims.Repo.SupplyOrderRepo;

@Service
public class RSOService implements IRSOService {

    private static final Logger logger = LoggerFactory.getLogger(RSOService.class);

    // necessary dependency Injections
    @Autowired
    private ReturnOrderRepo returnOrderRepo;

    @Autowired
    private RSORepo returnSupplyOrderRepo;

    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private DeliveryManRepo deliveryManRepo;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private SupplyOrderRepo supplyOrderRepo;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;
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
        returnSupplyOrder.setDateTime(formattedDateTime);
        
        // assign deliveryman to return supply order if deliveryman not available then rso status will be pending
        // String deliveryManId = assignDeliveryMan(returnSupplyOrder);

        // if (deliveryManId == null) {
        //     returnSupplyOrder.setStatus("pending");
        // }

        // returnSupplyOrder.setDeliveryManId(deliveryManId);
        

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
            returnSupplyOrder.setDeliveredDateTime(formattedDateTime);

            DeliveryMan m = deliveryManService.getDeliveryManById(returnSupplyOrder.getDeliveryManId());

            m.setStatus("available");

            try{
                deliveryManService.updateDeliveryMan(m);
            }
            catch(Exception e)
            {
                logger.error(e.getMessage(), e);
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
        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(returnSupplyOrder.getWarehouseId());
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
                    logger.error(e.getMessage(), e);
                    return null;
                }
                break;
            }
        }
        return deliveryManId;
    }
    public Map<String ,Object>getReturnSupplyOrderStatusSByDid(String id){
         if (id.equals("")) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!deliveryManRepo.existsById(id)) {
            throw new RuntimeException("DeliveryMan  with id " + id + " does not exist");
        }
        DeliveryMan deliveryMan =  deliveryManService.getDeliveryManById(id);
        if(deliveryMan == null)
        {
            logger.debug("Delivery man not exists");
            return null;
        }
        WareHouse wareHouse = wareHouseService.getWareHouseById( deliveryMan.getWarehouseId());
        if(wareHouse == null)
        {
            logger.debug("delivery man warehouse donot exists");
            return null;
        }
        List<ReturnSupplyOrder> orders = returnSupplyOrderRepo.findAll();
        Map<String, Object> Filterorders = new HashMap<>();

        for (ReturnSupplyOrder o : orders) {
            if (o.getStatus().equals("shipped") && o.getDeliveryManId().equals(id)) {                
                Supplier s = supplierService.getSupplierById(o.getSupplierId());
                User user = userService.getUserByUserId(o.getSupplierId());
                Product product = productService.getProductById(o.getProductId());
                   
                Filterorders.put("rso", o);                   
                Filterorders.put("supplier", s);
                Filterorders.put("warehouse", wareHouse);
                Filterorders.put("product",product);
                Filterorders.put("user",user);
                break;
                
            }
        }
        return Filterorders;
    }
    public List<Map<String ,Object>>getReturnSupplyOrderStatusCByDid(String id){
        if (id.equals("")) {
           throw new RuntimeException("Id shouldn't be null");
       } else if (!deliveryManRepo.existsById(id)) {
           throw new RuntimeException("DeliveryMan  with id " + id + " does not exist");
       }
       DeliveryMan deliveryMan =  deliveryManService.getDeliveryManById(id);
       if(deliveryMan == null)
       {
           logger.debug("Delivery man not exists");
           return null;
       }
       WareHouse wareHouse = wareHouseService.getWareHouseById( deliveryMan.getWarehouseId());
       if(wareHouse == null)
       {
           logger.debug("delivery man warehouse donot exists");
           return null;
       }
       List<ReturnSupplyOrder> orders = returnSupplyOrderRepo.findAll();
       List<Map<String ,Object>> ord = new ArrayList<>();

       for (ReturnSupplyOrder o : orders) {
           if (o.getStatus().equals("delivered") && o.getDeliveryManId().equals(id)) {                
               Supplier s = supplierService.getSupplierById(o.getSupplierId());
               User user = userService.getUserByUserId(o.getSupplierId());
               Product product = productService.getProductById(o.getProductId());
               Map<String, Object> Filterorders = new HashMap<>();
                  
               Filterorders.put("rso", o);                   
               Filterorders.put("supplier", s);
               Filterorders.put("warehouse", wareHouse);
               Filterorders.put("product",product);
               Filterorders.put("user",user);
                ord.add(Filterorders);
            //    break;
               
           }
       }
       return ord;
   }
    

    
}
