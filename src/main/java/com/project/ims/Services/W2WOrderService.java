package com.project.ims.Services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.ims.IServices.IW2WOrderService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.Product;
import com.project.ims.Models.W2WOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.W2WOrderRepo;

@Service
public class W2WOrderService implements IW2WOrderService {

    // necessary dependency injections
    @Autowired
    private W2WOrderRepo w2wOrderRepo;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private OrderRepo orderRepo;

    // Services

    @Override
    public List<W2WOrder> getAllW2WOrder() {
        return w2wOrderRepo.findAll();
    }

    @Override
    public W2WOrder getW2WOrderById(String id) {

        if (id == null || id.isEmpty())
        {
            throw new RuntimeException("W2W Order ID cannot be empty");
        }

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

        // set total amount
        Product product = productService.getProductById(w2wOrder.getProduct_id());

        int total_amount = Integer.parseInt(product.getPrice()) * Integer.parseInt(w2wOrder.getQuantity());

        w2wOrder.setTotal_amount(Integer.toString(total_amount));

        // ----------------------------------------------

        // reduce quantity from source warehouse

        WareHouse s_warehouse = wareHouseService.getWareHouseById(w2wOrder.getS_warehouse_id());

        for (int i = 0; i < s_warehouse.getProduct_ids().size(); i++) {
            if (s_warehouse.getProduct_ids().get(i).equals(w2wOrder.getProduct_id())) {
                int quantity = Integer.parseInt(s_warehouse.getQuantities().get(i));
                quantity = quantity - Integer.parseInt(w2wOrder.getQuantity());
                s_warehouse.getQuantities().set(i, Integer.toString(quantity));
                break;
            }
        }

        String deliveryMan = assignDeliveryMan(w2wOrder);

        if (deliveryMan == null) {
            System.out.println("Deliveryman not currently available");
            w2wOrder.setStatus("pending");
            return null;
        }

        try{
            wareHouseService.updateWareHouse(s_warehouse);
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        w2wOrder.setDate_time(formattedDateTime);

        return w2wOrderRepo.save(w2wOrder);
    }

    @Override
    public W2WOrder updateW2WOrder(W2WOrder w2wOrder) {

        if (w2wOrder == null)
        {
            throw new RuntimeException("W2W Order cannot be empty");
        }
        else if (!w2wOrderRepo.existsById(w2wOrder.getId()))
        {
            throw new RuntimeException("W2W Order with id " + w2wOrder.getId() + " does not exist");
        }

        return w2wOrderRepo.save(w2wOrder);
    }

    @Override
    public void deleteW2WOrder(String id) {

        if (id == null || id.isEmpty())
        {
            throw new RuntimeException("W2W Order ID cannot be empty");
        }
        else if (!w2wOrderRepo.existsById(id))
        {
            throw new RuntimeException("W2W Order with id " + id + " does not exist");
        }
        
        w2wOrderRepo.deleteById(id);
    }

    @Override
    public List<W2WOrder> getAllW2WOrderByOrderId(String orderId) {

        if (orderId == null || orderId.isEmpty())
        {
            throw new RuntimeException("Order ID cannot be empty");
        }

        return w2wOrderRepo.findByOrderId(orderId);
    }

    @Override
    public W2WOrder updateW2WOrderStatus(String id , String status) {

        W2WOrder w2wOrder = getW2WOrderById(id);

        if(status.equals("delivered"))
        {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            w2wOrder.setDelivered_date_time(formattedDateTime);

            DeliveryMan m = deliveryManService.getDeliveryManById(w2wOrder.getDelivery_man_id());

            m.setStatus("available");

            try{
                deliveryManService.updateDeliveryMan(m);
            }
            catch (Exception e) {
                System.out.println(e);
                return null;
            }
            
            // checking if all w2wOrders of the product are delivered then update status of order to shipped
            List<W2WOrder> w2wOrders = getAllW2WOrderByOrderId(w2wOrder.getOrderId());

            boolean allDelivered = true;

            for (W2WOrder w : w2wOrders) {
                if (!w.getStatus().equals("delivered")) {
                    allDelivered = false;
                    break;
                }
            }

            Order order = orderRepo.findById(w2wOrder.getOrderId()).orElse(null);

            if (allDelivered) {
                order.setStatus("shipped");

                String deliveryMan = assignDeliveryMan(order);

                if (deliveryMan == null) {
                    System.out.println("Deliveryman not currently available");
                    order.setStatus("pending");
                    return null;
                }

                try{
                    orderRepo.save(order);
                }
                catch(Exception e){
                    System.out.println(e);
                    return null;
                }
            }

        }
        else if (status.equals("cancel"))
        {
            DeliveryMan m = deliveryManService.getDeliveryManById(w2wOrder.getDelivery_man_id());

            m.setStatus("available");

            try{
                deliveryManService.updateDeliveryMan(m);
            }
            catch(Exception e){
                System.out.println(e);
                return null;
            }

            if(w2wOrder.getStatus().equals("shipped"))
            {
                WareHouse s_warehouse = wareHouseService.getWareHouseById(w2wOrder.getS_warehouse_id());

                for (int i = 0; i < s_warehouse.getProduct_ids().size(); i++) {
                    if (s_warehouse.getProduct_ids().get(i).equals(w2wOrder.getProduct_id())) {
                        int quantity = Integer.parseInt(s_warehouse.getQuantities().get(i));
                        quantity = quantity + Integer.parseInt(w2wOrder.getQuantity());
                        s_warehouse.getQuantities().set(i, Integer.toString(quantity));
                        break;
                    }
                }

                try{
                    wareHouseService.updateWareHouse(s_warehouse);
                }
                catch(Exception e){
                    System.out.println(e);
                    return null;
                }
            }
        }

        w2wOrder.setStatus(status);

        return w2wOrderRepo.save(w2wOrder);
    }

    // assigning deliveryman to w2worder and order
    
    public String assignDeliveryMan(W2WOrder w2wOrder)
    {
        String assigned_deliveryMan = null;
        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(w2wOrder.getS_warehouse_id());

        for (DeliveryMan d : deliveryMen) {
            if (d.getStatus().equals("available")) {
                d.setStatus("unavailable");
                assigned_deliveryMan = d.getId();
                try {
                    deliveryManService.updateDeliveryMan(d);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                break;
            }
        }

        return assigned_deliveryMan;
    }
    
    public String assignDeliveryMan(Order order)
    {
        String assigned_deliveryMan = null;
        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(order.getWarehouse_id());
            
        for (DeliveryMan d : deliveryMen) {
            if (d.getStatus().equals("available")) {
                d.setStatus("unavailable");
                assigned_deliveryMan = d.getId();
                try{
                    deliveryManService.updateDeliveryMan(d);
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
                
                break;
            }
        }

        return assigned_deliveryMan;
    }
}
