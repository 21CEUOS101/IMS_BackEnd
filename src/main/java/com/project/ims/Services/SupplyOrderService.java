package com.project.ims.Services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
// imports
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.ISupplyOrderService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Product;
import com.project.ims.Models.SupplyOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Repo.SupplyOrderRepo;

@Service
public class SupplyOrderService implements ISupplyOrderService {

    // necessary dependency Injections
    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private SupplyOrderRepo supplyOrderRepo;

    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private ProductService productService;

    // Services

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

        Product product = productService.getProductById(supplyOrder.getProduct_id());

        int price = product.getWhole_sale_price();
        
        int quantity = Integer.parseInt(supplyOrder.getQuantity());
        int total_amount = price * quantity;
        supplyOrder.setTotal_amount(Integer.toString(total_amount));

        supplyOrder.setStatus("pending");

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        supplyOrder.setDate_time(formattedDateTime);

        

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

        if (id == null) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!supplyOrderRepo.existsById(id)) {
            throw new RuntimeException("Supply Order with id " + id + " does not exist");
        }

        supplyOrderRepo.deleteById(id);
    }
    
    public String assignDeliveryMan(SupplyOrder supplyOrder) {
        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(supplyOrder.getWarehouse_id());
        String deliveryManId = null;
        for (DeliveryMan d : deliveryMen) {
            if (d.getStatus().equals("available")) {
                d.setStatus("unavailable");
                deliveryManId = d.getId();
                try {
                    deliveryManService.updateDeliveryMan(d);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }

                break;
            }
        }

        return deliveryManId;
    }
    
    @Override
    public SupplyOrder updateSupplyOrderStatus(String id , String status)
    {
        SupplyOrder supplyOrder = getSupplyOrderById(id);

        if (status.equals("delivered")) {

            WareHouse wareHouse = wareHouseService.getWareHouseById(supplyOrder.getWarehouse_id());

            for (int i = 0; i < wareHouse.getProduct_ids().size(); i++) {
                if (wareHouse.getProduct_ids().get(i).equals(supplyOrder.getProduct_id())) {
                    int quantity = Integer.parseInt(wareHouse.getQuantities().get(i));
                    quantity = quantity + Integer.parseInt(supplyOrder.getQuantity());
                    wareHouse.getQuantities().set(i, Integer.toString(quantity));
                }
            }

            try{
                wareHouseService.updateWareHouse(wareHouse);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }

            supplyOrder.setDelivered_date_time(LocalDateTime.now().toString());

            DeliveryMan m = deliveryManService.getDeliveryManById(supplyOrder.getDelivery_man_id());
    
            m.setStatus("available");
    
            try{
                deliveryManService.updateDeliveryMan(m);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }

        } else if (status.equals("cancel")) {
            if(supplyOrder.getStatus().equals("approved"))
            {
                DeliveryMan m = deliveryManService.getDeliveryManById(supplyOrder.getDelivery_man_id());

                m.setStatus("available");

                try {
                    deliveryManService.updateDeliveryMan(m);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
        }
        else if (status.equals("approved"))
        {
            String deliveryManId = assignDeliveryMan(supplyOrder);

            supplyOrder.setDelivery_man_id(deliveryManId);

            if (deliveryManId == null) {
                status = "pending";
            }
        }

        // checking if status is enum of pending, delivered or cancelled

        if (!(status.equals("pending") || status.equals("delivered") || status.equals("cancelled") || status.equals("approved")))
        {
            System.out.println("Invalid status");    
            return null;
        }

        supplyOrder.setStatus(status);

        return supplyOrderRepo.save(supplyOrder);
    }
    
}
