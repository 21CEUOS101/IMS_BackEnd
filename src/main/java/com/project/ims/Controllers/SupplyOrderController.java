package com.project.ims.Controllers;

// imports
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.SupplyOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Requests.SupplyOrderAddRequest;
import com.project.ims.Requests.SupplyOrderUpdateRequest;
import com.project.ims.Services.DeliveryManService;
import com.project.ims.Services.ProductService;
import com.project.ims.Services.SupplyOrderService;
import com.project.ims.Services.WareHouseService;

@RestController
@RequestMapping("/api")
public class SupplyOrderController {

    // necessary dependency injections
    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private SupplyOrderService supplyOrderService;
    
    // controllers

    // get all supply orders
    @GetMapping("/supply-order")
    public List<SupplyOrder> getAllSupplyOrders() {
        try{
            List<SupplyOrder> supplyOrders = supplyOrderService.getAllSupplyOrder();
            return supplyOrders;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/supply-order/{id}")
    public SupplyOrder getSupplyOrder(@PathVariable String id) {
        try{
            SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(id);
            return supplyOrder;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping("/supply-order")
    public SupplyOrder addSupplyOrder(@RequestBody SupplyOrderAddRequest data) {

        String id = generateId();
        SupplyOrder supplyOrder = new SupplyOrder();
        supplyOrder.setId(id);
        supplyOrder.setProduct_id(data.getProduct_id());
        supplyOrder.setQuantity(data.getQuantity());
        supplyOrder.setSupplier_id(data.getSupplier_id());
        supplyOrder.setWarehouse_id(data.getWarehouse_id());
        supplyOrder.setPayment_method(data.getPayment_method());

        if (data.getPayment_method().equals("online")) {
            supplyOrder.setTransaction_id(data.getTransaction_id());
        }

        supplyOrder.setPickup_address(data.getPickup_address());

        int price = Integer.parseInt(productService.getProductById(data.getProduct_id()).getPrice());
        int quantity = Integer.parseInt(data.getQuantity());
        int total_amount = price * quantity;
        supplyOrder.setTotal_amount(Integer.toString(total_amount));

        supplyOrder.setStatus("Pending");

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        supplyOrder.setDate_time(formattedDateTime);

        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(supplyOrder.getWarehouse_id());
            
        for (DeliveryMan d : deliveryMen) {
            if (d.getStatus().equals("available")) {
                d.setStatus("unavailable");
                supplyOrder.setDelivery_man_id(d.getId());
                
                try{
                    deliveryManService.updateDeliveryMan(d);
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                    return null;
                }
                
                break;
            }
        }

        try{
            supplyOrderService.addSupplyOrder(supplyOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return supplyOrder;
    }
    
    @PostMapping("/supply-order/{id}/status")
    public SupplyOrder updateSupplyOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status) {
        SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(id);

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

        } else if (status.equals("cancelled")) {
            // do nothing
        }

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

        // checking if status is enum of pending, delivered or cancelled

        if (!(status.equals("pending") || status.equals("delivered") || status.equals("cancelled")))
        {
            System.out.println("Invalid status");    
            return null;
        }

        supplyOrder.setStatus(status);

        try{
            supplyOrderService.updateSupplyOrder(supplyOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return supplyOrder;
    }

    // update supply order
    @PostMapping("/supply-order/{id}")
    public SupplyOrder updateSupplyOrder(@PathVariable("id") String id, @RequestBody SupplyOrderUpdateRequest data) {
        SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(id);
        supplyOrder.setDate_time(data.getDate_time());
        supplyOrder.setDelivered_date_time(data.getDelivered_date_time());
        supplyOrder.setDelivery_man_id(data.getDelivery_man_id());
        supplyOrder.setPayment_method(data.getPayment_method());
        supplyOrder.setPickup_address(data.getPickup_address());
        supplyOrder.setProduct_id(data.getProduct_id());
        supplyOrder.setQuantity(data.getQuantity());
        supplyOrder.setStatus(data.getStatus());
        supplyOrder.setSupplier_id(data.getSupplier_id());
        supplyOrder.setTotal_amount(data.getTotal_amount());
        supplyOrder.setTransaction_id(data.getTransaction_id());
        supplyOrder.setWarehouse_id(data.getWarehouse_id());
        
        try{
            supplyOrderService.updateSupplyOrder(supplyOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return supplyOrder;
    }
    
    @DeleteMapping("/supply-order/{id}")
    public void deleteSupplyOrder(@PathVariable String id) {

        try{
            supplyOrderService.deleteSupplyOrder(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    // generate id
    public String generateId() {
        Random rand = new Random();
        int random = rand.nextInt(1000000);
        String id = "so" + random;
        return id;
    }
}
