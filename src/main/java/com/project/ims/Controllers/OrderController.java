package com.project.ims.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// imports
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Objects.CustomerUserPair;
import com.project.ims.Requests.OrderAddRequest;
import com.project.ims.Requests.OrderUpdateRequest;
import com.project.ims.Services.DeliveryManService;
import com.project.ims.Services.OrderService;
import com.project.ims.Utils.IdGenerator;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173","https://ashish2901-ims.vercel.app/","http://localhost:3000","http://localhost:3001","https://ims-frontend-eight.vercel.app/"}, allowedHeaders = "*", allowCredentials = "true")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    // necessary dependency injections

    @Autowired
    private OrderService orderService;


    // Order APIs
    @Autowired
    private DeliveryManService deliveryManService;


    // Get Order by ID
    @GetMapping("/order/{id}")
    public Order getOrderById(@PathVariable("id") String id) {

        try{
            Order order = orderService.getOrderById(id);
            return order;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    // Get all orders
    @GetMapping("/order")
    public List<Order> getAllOrders() {

        try{
            List<Order> orders = orderService.getAllOrder();
            return orders;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    // Get all orders by customer id
    @GetMapping("/orders/customer/{id}")
    public List<Order> getAllOrdersByCustomerId(@PathVariable("id") String id) {

        try{
            List<Order> orders = orderService.getAllOrderByCustomerId(id);
            return orders;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }

    }
    
    // create order
    @PostMapping("/order")
    public List<Order> addOrder(@RequestBody OrderAddRequest data) {

        List<Order> orders = new ArrayList<>();

        // getting product ids and quantities
        List<String> product_ids = data.getProductIds();
        List<String> quantities = data.getQuantities();

        if(product_ids.size() != quantities.size())
        {
            logger.warn("Product ids and quantities are not equal");
            return null;
        }

        // list of warehouse ids
        String warehouse_id = data.getWarehouseId();

        // making all orders separate and adding them to the list of orders
        for(int i=0;i<product_ids.size();i++)
        {
            try{
                createOrder(product_ids, quantities, warehouse_id, data, orders, i);
            }
            catch(Exception e)
            {
                logger.error(e.getMessage(), e);
            }
        }
        
        return orders;
    }
    
    @PostMapping("/order/{id}/status")
    public Order updateOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status) {

        try{
            Order order = orderService.updateOrderStatus(id, status);
            return order;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    
    // update order
    @PostMapping("/order/{id}")
    public Order updateOrder(@PathVariable("id") String id, @RequestBody OrderUpdateRequest data) {

        Order order = orderService.getOrderById(id);
        order.setCustomerId(data.getCustomerId());
        order.setProductId(data.getProductId());
        order.setQuantity(data.getQuantity());
        order.setWarehouseId(data.getWarehouseId());
        order.setTotalAmount(data.getTotalAmount());
        order.setDateTime(data.getDateTime());
        order.setPaymentMethod(data.getPaymentMethod());
        order.setStatus(data.getStatus());
        order.setDeliveryAddress(data.getDeliveryAddress());
        order.setDeliveryManId(data.getDeliveryManId());
        order.setDeliveredDateTime(data.getDeliveredDateTime());
        
        if("online".equals(order.getPaymentMethod()))
        {
            order.setTransactionId(data.getTransactionId());
        }

        try{
            orderService.updateOrder(order);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }

        return order;
        
    }
    
    @DeleteMapping("/order/{id}")
    public void deleteOrder(@PathVariable String id)
    {
        try{
            orderService.deleteOrder(id);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    @GetMapping("/order/orderstatusCByDId/{id}")
    public List<Map<String, Object>> orderstatusCByDId(@PathVariable("id") String id) {
        try {
            List<Map<String, Object>> ordersWithCustomer = orderService.orderstatusCByDeliverymanId(id);
            return ordersWithCustomer;
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/order/orderstatusPByDId/{id}")
    public List<Map<String, Object>> orderstatusPByDId(@PathVariable("id") String id) {
        try {
            List<Map<String, Object>> ordersWithCustomer = orderService.orderstatusPByDeliverymanId(id);
            return ordersWithCustomer;
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/order/orderstatusSByDId/{id}")
    public Map<String, Object> orderstatusSByDId(@PathVariable("id") String id) {
        try {
            Map<String, Object> ordersWithCustomer = orderService.orderstatusSByDeliverymanId(id);
            return ordersWithCustomer;
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @PostMapping("/order/assignBydeliveryman/{id}/data")
    public Order assignDeliverymanById(@PathVariable("id") String id,@RequestParam("data") String data) {
    //    System.out.println(data);
        try {
           Order order = orderService.getOrderById(data);
           if(order == null){
            logger.debug("doesnot exist delivery man or warehouse");
            return null;
           }
           DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
           

           if(order.getStatus().equals("pending") &&  deliveryMan.getStatus().equals("available")){

            order.setDeliveryManId(id);
               order.setStatus("shipped");
               
               orderService.updateOrder(order);
               deliveryMan.setStatus("unavailable");
               deliveryManService.updateDeliveryMan(deliveryMan);
               return order;
           }
           else{
            logger.debug("delivery man is not available");
            return null;

           }
           
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    } 
    @GetMapping("/order/numberofcustomer/{id}")
    public List<CustomerUserPair> getNumberOfCustomer(@PathVariable("id") String id) {
        try {

           List<CustomerUserPair> allCustomers = orderService.numberofcustomerByDId(id);
           return allCustomers;
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/order/totalordercompletedByDid/{id}")
    public String numberofCompletedorders(@PathVariable("id") String id) {
        try {

            return String.valueOf(orderstatusCByDId(id).size());

        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return "error";
        }
    }
    @GetMapping("/order/totalorderCancelByDid/{id}")
    public List<Map<String, Object>> numberofCancelorders(@PathVariable("id") String id) {
        try {
           
            List<Map<String, Object>>  allcancel = orderService.numberofCancelorders(id);     
           return allcancel;      
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    } 

    public void createOrder(List<String> product_ids, List<String> quantities, String warehouse_id, OrderAddRequest data,
            List<Order> orders, int i) 
    {
        String id = IdGenerator.generate("o");
        Order order = new Order();
        order.setId(id);
        order.setProductId(product_ids.get(i));
        order.setQuantity(quantities.get(i));
        order.setCustomerId(data.getCustomerId());
        order.setPaymentMethod(data.getPaymentMethod());
        if ("online".equals(data.getPaymentMethod())) {
            order.setTransactionId(data.getTransactionId());
        }
        order.setDeliveryAddress(data.getDeliveryAddress());
        order.setStatus("pending");
        order.setWarehouseId(warehouse_id);
        orderService.addOrder(order);
        orders.add(order);
    }
}
