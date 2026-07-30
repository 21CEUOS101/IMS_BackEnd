package com.project.ims.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// imports
import java.util.List;
import java.util.Map;
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
import com.project.ims.Utils.IdGenerator;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173","https://ashish2901-ims.vercel.app/","http://localhost:3000","http://localhost:3001","https://ims-frontend-eight.vercel.app/"}, allowedHeaders = "*", allowCredentials = "true")
public class RSOController {

    private static final Logger logger = LoggerFactory.getLogger(RSOController.class);

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
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @PostMapping("/return-supply-order")
    public ReturnSupplyOrder addReturnSupplyOrder(@RequestBody RSOAddRequest data) {

        String id = IdGenerator.generate("rso");
        ReturnSupplyOrder returnSupplyOrder = new ReturnSupplyOrder();
        returnSupplyOrder.setId(id);

        
        Order order = orderRepo.findById(data.getOrderId()).orElse(null);
        
        if (order == null) {
            logger.debug("Order not found");
            return null;
        }

        returnSupplyOrder.setOrderId(data.getOrderId());
        returnSupplyOrder.setWarehouseId(order.getWarehouseId());
        returnSupplyOrder.setProductId(order.getProductId());
        returnSupplyOrder.setQuantity(order.getQuantity());
        returnSupplyOrder.setRefundAmount(order.getTotalAmount());
        returnSupplyOrder.setDeliveryAddress(data.getDeliveryAddress());
        returnSupplyOrder.setReturnReason(data.getReturnReason());
        returnSupplyOrder.setStatus("shipped");
        returnSupplyOrder.setSupplierId(data.getSupplierId());
        
        try{
            returnSupplyOrderService.addReturnSupplyOrder(returnSupplyOrder);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
            return null;
        }

    }
    
    @PostMapping("/return-supply-order/{id}")
    public ReturnSupplyOrder updateReturnSupplyOrder(@PathVariable("id") String id,
            @RequestBody RSOUpdateRequest data) {
        ReturnSupplyOrder returnSupplyOrder = returnSupplyOrderService.getReturnSupplyOrderById(id);
        returnSupplyOrder.setStatus(data.getStatus());
        returnSupplyOrder.setDateTime(data.getDateTime());
        returnSupplyOrder.setDeliveredDateTime(data.getDeliveredDateTime());
        returnSupplyOrder.setDeliveryManId(data.getDeliveryManId());
        returnSupplyOrder.setOrderId(data.getOrderId());
        returnSupplyOrder.setProductId(data.getProductId());
        returnSupplyOrder.setQuantity(data.getQuantity());
        returnSupplyOrder.setRefundAmount(data.getRefundAmount());
        returnSupplyOrder.setWarehouseId(data.getWarehouseId());
        returnSupplyOrder.setDeliveryAddress(data.getDeliveryAddress());
        returnSupplyOrder.setReturnReason(data.getReturnReason());
        returnSupplyOrder.setSupplierId(data.getSupplierId());
        
        try{
            returnSupplyOrderService.updateReturnSupplyOrder(returnSupplyOrder);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
        }
    }

    @GetMapping("/return-supply-order/statusSByDid/{id}")
    public Map<String,Object> getReturnSupplyOrderStatusSByDid(@PathVariable("id") String id) {
        try{
            Map<String,Object> returnSupplyOrder = returnSupplyOrderService.getReturnSupplyOrderStatusSByDid(id);
            return returnSupplyOrder;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/return-supply-order/statusCByDid/{id}")
    public List<Map<String,Object>> getReturnSupplyOrderStatusCByDid(@PathVariable("id") String id) {
        try{
            List<Map<String,Object>> returnSupplyOrder = returnSupplyOrderService.getReturnSupplyOrderStatusCByDid(id);
            return returnSupplyOrder;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
