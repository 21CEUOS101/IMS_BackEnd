package com.project.ims.Controllers;

// imports
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.project.ims.Models.Customer;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.W2WOrder;
import com.project.ims.Requests.OrderAddRequest;
import com.project.ims.Requests.OrderUpdateRequest;
import com.project.ims.Services.DeliveryManService;
import com.project.ims.Services.OrderService;
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
public class OrderController {

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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return null;
        }

    }
    
    // create order
    @PostMapping("/order")
    public List<Order> addOrder(@RequestBody OrderAddRequest data) {

        List<Order> orders = new ArrayList<>();

        // getting product ids and quantities
        List<String> product_ids = data.getProduct_ids();
        List<String> quantities = data.getQuantities();

        if(product_ids.size() != quantities.size())
        {
            System.out.println("Product ids and quantities are not equal");
            return null;
        }

        // list of warehouse ids
        String warehouse_id = data.getWarehouse_id();

        // making all orders separate and adding them to the list of orders
        for(int i=0;i<product_ids.size();i++)
        {
            try{
                createOrder(product_ids, quantities, warehouse_id, data, orders, i);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    // update order
    @PostMapping("/order/{id}")
    public Order updateOrder(@PathVariable("id") String id, @RequestBody OrderUpdateRequest data) {

        Order order = orderService.getOrderById(id);
        order.setCustomerId(data.getCustomer_id());
        order.setProduct_id(data.getProduct_id());
        order.setQuantity(data.getQuantity());
        order.setWarehouse_id(data.getWarehouse_id());
        order.setTotal_amount(data.getTotal_amount());
        order.setDate_time(data.getDate_time());
        order.setPayment_method(data.getPayment_method());
        order.setStatus(data.getStatus());
        order.setDelivery_address(data.getDelivery_address());
        order.setDelivery_man_id(data.getDelivery_man_id());
        order.setDelivered_date_time(data.getDelivered_date_time());
        
        if(order.getPayment_method().equals("online"))
        {
            order.setTransaction_id(data.getTransaction_id());
        }

        try{
            orderService.updateOrder(order);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
    }

    @GetMapping("/order/orderstatusCByDId/{id}")
    public List<Map<String, Object>> orderstatusCByDId(@PathVariable("id") String id) {
        try {
            List<Map<String, Object>> ordersWithCustomer = orderService.orderstatusCByDeliverymanId(id);
            return ordersWithCustomer;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/order/orderstatusPByDId/{id}")
    public List<Map<String, Object>> orderstatusPByDId(@PathVariable("id") String id) {
        try {
            List<Map<String, Object>> ordersWithCustomer = orderService.orderstatusPByDeliverymanId(id);
            return ordersWithCustomer;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/order/orderstatusSByDId/{id}")
    public Map<String, Object> orderstatusSByDId(@PathVariable("id") String id) {
        try {
            Map<String, Object> ordersWithCustomer = orderService.orderstatusSByDeliverymanId(id);
            return ordersWithCustomer;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @PostMapping("/order/assignBydeliveryman/{id}/data")
    public Order assignDeliverymanById(@PathVariable("id") String id,@RequestParam("data") String data) {
    //    System.out.println(data);
        try {
           Order order = orderService.getOrderById(data);
           if(order == null){
            System.out.println("doesnot exist delivery man or warehouse");
            return null;
           }
           DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
           

           if(order.getStatus().equals("pending")){

            order.setDelivery_man_id(id);
               order.setStatus("shipped");
               
               orderService.updateOrder(order);
               deliveryMan.setStatus("unavailable");
               deliveryManService.updateDeliveryMan(deliveryMan);
               return order;
           }
           else{
            System.out.println("delivery man is not available");
            return null;

           }
           
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    } 
    @GetMapping("/order/numberofcustomer/{id}")
    public HashSet<Customer> Numberofcustomer(@PathVariable("id") String id) {
        try {
           
           HashSet<Customer>  allCustomers = orderService.numberofcustomerByDId(id);     
           return allCustomers;      
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    } 
    @GetMapping("/order/totalordercompletedByDid/{id}")
    public int numberofCompletedorders(@PathVariable("id") String id) {
        try {
           
            return orderstatusCByDId(id).size();
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    } 
    @GetMapping("/order/totalorderCancelByDid/{id}")
    public List<Order> numberofCancelorders(@PathVariable("id") String id) {
        try {
           
            List<Order>  allcancel = orderService.numberofCancelorders(id);     
           return allcancel;      
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    } 
  
    public String generateOrderId()
    {
        Random rand = new Random();
        int random = rand.nextInt(1000000);
        String id = 'o' + String.valueOf(random);
        return id;
    }

    public void createOrder(List<String> product_ids, List<String> quantities, String warehouse_id, OrderAddRequest data,
            List<Order> orders, int i) 
    {

        String id = generateOrderId();
        Order order = new Order();
        order.setId(id);
        order.setProduct_id(product_ids.get(i));
        order.setQuantity(quantities.get(i));
        order.setCustomerId(data.getCustomer_id());
        order.setPayment_method(data.getPayment_method());
        if (data.getPayment_method().equals("online")) {
            order.setTransaction_id(data.getTransaction_id());
        }
        order.setDelivery_address(data.getDelivery_address());
        order.setStatus("pending");
        order.setWarehouse_id(warehouse_id);
        orderService.addOrder(order);
        orders.add(order);
    }
}
