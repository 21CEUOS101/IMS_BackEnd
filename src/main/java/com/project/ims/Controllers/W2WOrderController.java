package com.project.ims.Controllers;

// imports
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
import com.project.ims.Models.W2WOrder;
import com.project.ims.Requests.W2WOrderAddRequest;
import com.project.ims.Requests.W2WOrderUpdateRequest;
import com.project.ims.Services.W2WOrderService;

@RestController
@RequestMapping("/api")
public class W2WOrderController {

    // necessary dependency injections
    @Autowired
    private W2WOrderService w2wOrderService;

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
        w2wOrder.setOrderId(data.getOrderId());
        w2wOrder.setStatus("shipped");

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
    public W2WOrder updateW2WOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status) {
        try{
            W2WOrder w2wOrder  = w2wOrderService.updateW2WOrderStatus(id, status);
            return w2wOrder;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
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
        w2wOrder.setOrderId(data.getOrderId());
        
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
