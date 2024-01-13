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
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Product;
import com.project.ims.Models.W2WOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Requests.W2WOrderAddRequest;
import com.project.ims.Requests.W2WOrderUpdateRequest;
import com.project.ims.Services.DeliveryManService;
import com.project.ims.Services.ProductService;
import com.project.ims.Services.W2WOrderService;
import com.project.ims.Services.WareHouseService;

@RestController
@RequestMapping("/api")
public class W2WOrderController {

    // necessary dependency injections
    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private W2WOrderService w2wOrderService;

    @Autowired
    private ProductService productService;

    // controllers

    @GetMapping("/w2worder")
    public List<W2WOrder> getAllW2WOrders() {
        
        try{
            List<W2WOrder> w2wOrders = w2wOrderService.getAllW2WOrder();
            return w2wOrders;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    @GetMapping("/w2worder/{id}")
    public W2WOrder getW2WOrderById(@PathVariable("id") String id) {
        try{
            W2WOrder w2wOrder = w2wOrderService.getW2WOrderById(id);
            return w2wOrder;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    @PostMapping("/w2worder")
    public W2WOrder addW2WOrder(@RequestBody W2WOrderAddRequest data) {

        String id = generateId();
        W2WOrder w2wOrder = new W2WOrder();
        w2wOrder.setId(id);
        w2wOrder.setProduct_id(data.getProduct_id());
        w2wOrder.setQuantity(data.getQuantity());
        w2wOrder.setR_warehouse_id(data.getR_warehouse_id());
        w2wOrder.setS_warehouse_id(data.getS_warehouse_id());
        w2wOrder.setStatus("pending");

        // set total amount
        Product product = productService.getProductById(w2wOrder.getProduct_id());

        int total_amount = Integer.parseInt(product.getPrice()) * Integer.parseInt(w2wOrder.getQuantity());

        w2wOrder.setTotal_amount(Integer.toString(total_amount));

        // ----------------------------------------------

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        w2wOrder.setDate_time(formattedDateTime);

        try{
            w2wOrderService.addW2WOrder(w2wOrder);
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }

        return w2wOrder;
    }

    @PostMapping("/w2worder/{id}/status")
    public W2WOrder updateW2WOrderStatus(@PathVariable("id") String id, @RequestBody String status) {

        W2WOrder w2wOrder = w2wOrderService.getW2WOrderById(id);

        if(status.equals("delivered"))
        {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            w2wOrder.setDelivered_date_time(formattedDateTime);

            WareHouse r_warehouse = wareHouseService.getWareHouseById(w2wOrder.getR_warehouse_id());

            for(int i=0;i<r_warehouse.getProduct_ids().size();i++)
            {
                if (r_warehouse.getProduct_ids().get(i).equals(w2wOrder.getProduct_id())) {
                    int quantity = Integer.parseInt(r_warehouse.getQuantities().get(i));
                    quantity = quantity + Integer.parseInt(w2wOrder.getQuantity());
                    r_warehouse.getQuantities().set(i, Integer.toString(quantity));
                    break;
                } else {
                    r_warehouse.getProduct_ids().add(w2wOrder.getProduct_id());
                    r_warehouse.getQuantities().add(w2wOrder.getQuantity());
                }
            }
            
            try{
                wareHouseService.updateWareHouse(r_warehouse);
            }
            catch(Exception e){
                System.out.println(e);
                return null;
            }

            DeliveryMan m = deliveryManService.getDeliveryManById(w2wOrder.getDelivery_man_id());

            m.setStatus("available");

            try{
                deliveryManService.updateDeliveryMan(m);
            }
            catch(Exception e){
                System.out.println(e);
                return null;
            }
        }
        else if (status.equals("shipped"))
        {
            WareHouse s_warehouse = wareHouseService.getWareHouseById(w2wOrder.getS_warehouse_id());

            for (int i = 0; i < s_warehouse.getProduct_ids().size(); i++) {
                if (s_warehouse.getProduct_ids().get(i).equals(w2wOrder.getProduct_id())) {
                    int quantity = Integer.parseInt(s_warehouse.getQuantities().get(i));
                    quantity = quantity - Integer.parseInt(w2wOrder.getQuantity());
                    s_warehouse.getQuantities().set(i, Integer.toString(quantity));
                    break;
                }
            }

            List<DeliveryMan> deliveryMans = deliveryManService.getAllDeliveryManByWarehouse(s_warehouse.getId());

            for (DeliveryMan m : deliveryMans) {
                if (m.getStatus() == "available") {
                    m.setStatus("unavailable");
                    
                    try{
                        deliveryManService.updateDeliveryMan(m);
                    }
                    catch (Exception e) {
                        System.out.println(e);
                        return null;
                    }
                    
                    w2wOrder.setDelivery_man_id(m.getId());
                }
            }

            if (w2wOrder.getDelivery_man_id() == null) {
                System.out.println("Deliveryman not currently available");
                return null;
            }

            try{
                wareHouseService.updateWareHouse(s_warehouse);
            }
            catch(Exception e){
                System.out.println(e);
                return null;
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

        try{
            w2wOrderService.updateW2WOrder(w2wOrder);
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }

        return w2wOrder;
    }
    
    @PostMapping("/w2worder/{id}")
    public W2WOrder updateW2wOrder( @PathVariable("id") String id ,@RequestBody W2WOrderUpdateRequest data)
    {
        W2WOrder w2wOrder = w2wOrderService.getW2WOrderById(id);
        w2wOrder.setProduct_id(data.getProduct_id());
        w2wOrder.setQuantity(data.getQuantity());
        w2wOrder.setR_warehouse_id(data.getR_warehouse_id());
        w2wOrder.setS_warehouse_id(data.getS_warehouse_id());
        w2wOrder.setTotal_amount(data.getTotal_amount());
        w2wOrder.setStatus(data.getStatus());
        w2wOrder.setDate_time(data.getDate_time());
        w2wOrder.setDelivered_date_time(data.getDelivered_date_time());
        w2wOrder.setDelivery_man_id(data.getDelivery_man_id());
        
        try{
            w2wOrderService.updateW2WOrder(w2wOrder);
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }

        return w2wOrder;
    }

    @DeleteMapping("/w2worder/{id}")
    public void deleteW2WOrder(@PathVariable("id") String id) {
        try{
            w2wOrderService.deleteW2WOrder(id);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public String generateId() {
        Random rand = new Random();
        String id = "w2w" + rand.nextInt(1000000);
        return id;
    }
}
