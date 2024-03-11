package com.project.ims.Controllers;

// imports
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.Models.Order;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Requests.RSOAddRequest;
import com.project.ims.Requests.RSOUpdateRequest;
import com.project.ims.Services.RSOService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
public class RSOController {

    // necessary dependency injections

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private RSOService returnSupplyOrderService;

    // controllers

    @GetMapping("/return-supply-order")
    public List<ReturnSupplyOrder> getReturnSupplyOrders() {
        try{
            List<ReturnSupplyOrder> returnSupplyOrders = returnSupplyOrderService.getAllReturnSupplyOrder();
            return returnSupplyOrders;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/return-supply-order/{id}")
    public ReturnSupplyOrder getReturnSupplyOrderById(@PathVariable("id") String id) {
        try{
            ReturnSupplyOrder returnSupplyOrder = returnSupplyOrderService.getReturnSupplyOrderById(id);
            return returnSupplyOrder;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping("/return-supply-order")
    public ReturnSupplyOrder addReturnSupplyOrder(@RequestBody RSOAddRequest data) {

        String id = generateId();
        ReturnSupplyOrder returnSupplyOrder = new ReturnSupplyOrder();
        returnSupplyOrder.setId(id);

        
        Order order = orderRepo.findById(data.getOrder_id()).orElse(null);
        
        if (order == null) {
            System.out.println("Order not found");
            return null;
        }

        returnSupplyOrder.setOrder_id(data.getOrder_id());
        returnSupplyOrder.setWarehouse_id(order.getWarehouseId());
        returnSupplyOrder.setProduct_id(order.getProduct_id());
        returnSupplyOrder.setQuantity(order.getQuantity());
        returnSupplyOrder.setRefund_amount(order.getTotal_amount());
        returnSupplyOrder.setDelivery_address(data.getDelivery_address());
        returnSupplyOrder.setReturn_reason(data.getReturn_reason());
        returnSupplyOrder.setStatus("shipped");
        returnSupplyOrder.setSupplierId(data.getSupplier_id());
        
        try{
            returnSupplyOrderService.addReturnSupplyOrder(returnSupplyOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return returnSupplyOrder;
    }

    @PostMapping("/return-supply-order/{id}/status")
    public ReturnSupplyOrder updateReturnSupplyOrderStatus(@PathVariable("id") String id,
            @RequestParam("status") String status) {
        
        try{
            ReturnSupplyOrder returnSupplyOrder = returnSupplyOrderService.updateReturnSupplyOrderStatus(id, status);
            return returnSupplyOrder;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

    }
    
    @PostMapping("/return-supply-order/{id}")
    public ReturnSupplyOrder updateReturnSupplyOrder(@PathVariable("id") String id,
            @RequestBody RSOUpdateRequest data) {
        ReturnSupplyOrder returnSupplyOrder = returnSupplyOrderService.getReturnSupplyOrderById(id);
        returnSupplyOrder.setStatus(data.getStatus());
        returnSupplyOrder.setDate_time(data.getDate_time());
        returnSupplyOrder.setDelivered_date_time(data.getDelivered_date_time());
        returnSupplyOrder.setDelivery_man_id(data.getDelivery_man_id());
        returnSupplyOrder.setOrder_id(data.getOrder_id());
        returnSupplyOrder.setProduct_id(data.getProduct_id());
        returnSupplyOrder.setQuantity(data.getQuantity());
        returnSupplyOrder.setRefund_amount(data.getRefund_amount());
        returnSupplyOrder.setWarehouse_id(data.getWarehouse_id());
        returnSupplyOrder.setDelivery_address(data.getDelivery_address());
        returnSupplyOrder.setReturn_reason(data.getReturn_reason());
        returnSupplyOrder.setSupplierId(data.getSupplier_id());
        
        try{
            returnSupplyOrderService.updateReturnSupplyOrder(returnSupplyOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return returnSupplyOrder;
    }
    
    @DeleteMapping("/return-supply-order/{id}")
    public void deleteReturnSupplyOrder(@PathVariable("id") String id) {
        try{
            returnSupplyOrderService.deleteReturnSupplyOrder(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @GetMapping("/return-supply-orderstatusSByDid/{id}")
    public Map<String,Object> getReturnSupplyOrderStatusSByDid(@PathVariable("id") String id) {
        try{
            Map<String,Object> returnSupplyOrder = returnSupplyOrderService.getReturnSupplyOrderStatusSByDid(id);
            return returnSupplyOrder;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/return-supply-orderstatusCByDid/{id}")
    public List<Map<String,Object>> getReturnSupplyOrderStatusCByDid(@PathVariable("id") String id) {
        try{
            List<Map<String,Object>> returnSupplyOrder = returnSupplyOrderService.getReturnSupplyOrderStatusCByDid(id);
            return returnSupplyOrder;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String generateId() {
        // id generation
        Random rand = new Random();
        String id = "rso" + String.valueOf(rand.nextInt(1000000));
        return id;
    }
}
