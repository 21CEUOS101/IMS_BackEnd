package com.project.ims.Controllers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.time.format.DateTimeFormatter;
import com.project.ims.IServices.IAdminService;
import com.project.ims.IServices.ICustomerService;
import com.project.ims.IServices.IDeliveryManService;
import com.project.ims.IServices.IOrderService;
import com.project.ims.IServices.IProductService;
import com.project.ims.IServices.ISupplierService;
import com.project.ims.IServices.IWManagerService;
import com.project.ims.IServices.IWareHouseService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.Product;
import com.project.ims.Models.WareHouse;
import com.project.ims.Requests.OrderAddRequest;
import com.project.ims.Requests.OrderUpdateRequest;

import java.time.LocalDateTime;
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

    // Autowiring all the services
    
    @Autowired
    private IAdminService adminService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IDeliveryManService deliveryManService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IProductService productService;

    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private IWareHouseService wareHouseService;

    @Autowired
    private IWManagerService wManagerService;


    // Order APIs


    // Get Order by ID
    @GetMapping("/order/{id}")
    public Order getOrderById(@PathVariable("id") String id) {

        Order order = orderService.getOrderById(id);

        return order;
    }

    // Get all orders

    @GetMapping("/orders")
    public List<Order> getAllOrders() {

        List<Order> orders = orderService.getAllOrder();

        return orders;
    }

    // Get all orders by customer id

    @GetMapping("/orders/customer/{id}")
    public List<Order> getAllOrdersByCustomerId(@PathVariable("id") String id) {

        List<Order> orders = orderService.getAllOrderByCustomerId(id);

        return orders;
    }
    
    // create order
    @PostMapping("/order")
    public List<Order> createOrder(@RequestBody OrderAddRequest data) {

        List<Order> orders = new ArrayList<>();

        // gettting product ids and quantities
        List<String> product_ids = data.getProduct_ids();
        List<String> quantities = data.getQuantities();

        // list of warehouse ids
        List<String> warehouse_ids = data.getWarehouse_ids();


        if(product_ids.size() != quantities.size())
        {
            throw new RuntimeException("Invalid Request");
        }

        // making all orders separate and adding them to the list of orders
        for(int i=0;i<product_ids.size();i++)
        {
            Order order = new Order();

            // generating random id for order
            Random rand = new Random();
            int random = rand.nextInt(1000000);
            String id = "o" + String.valueOf(random);

            order.setId(id);
            order.setProduct_id(product_ids.get(i));
            order.setQuantity(quantities.get(i));
            order.setCustomer_id(data.getCustomer_id());

            // set total amount

            Integer totalPrice = 0;

            String product_id = order.getProduct_id();
            String quantity = order.getQuantity();

            Integer price = Integer.parseInt(productService.getProductById(product_id).getPrice());
            Integer q = Integer.parseInt(quantity);

            totalPrice += price * q;

            order.setTotal_amount(totalPrice.toString());

            // set time and date

            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            order.setDate_time(formattedDateTime);

            // set payment method
            order.setPayment_method(data.getPayment_method());

            if (data.getPayment_method().equals("online")) {
                order.setTransaction_id(data.getTransaction_id());
            }

            // set delivery address
            order.setDelivery_address(data.getDelivery_address());

            // set status
            order.setStatus("pending");

            // set warehouse id
            order.setWarehouse_id(warehouse_ids.get(i));

            orders.add(order);

            orderService.addOrder(order);
        }
        
        return orders;
    }
    
    @PostMapping("/order/{id}/status")
    public Order updateOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status,
            @RequestParam("deliveryman_id") String deliveryman_id) {

        Order order = orderService.getOrderById(id);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        
        if (status.equals("delivered")) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            order.setDelivered_date_time(formattedDateTime);
            
            DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(deliveryman_id);
            
            deliveryMan.setStatus("available");
            
            deliveryManService.updateDeliveryMan(deliveryMan);
        } else if (status.equals("shipped")) {
            
            // assigning delivery man to order
            
            List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(order.getWarehouse_id());
            
            for (DeliveryMan d : deliveryMen) {
                if (d.getStatus() == "available") {
                    d.setStatus("unavailable");
                    order.setDelivery_man_id(deliveryman_id);
                    deliveryManService.updateDeliveryMan(d);
                    break;
                }
            }
            
            if (order.getDelivery_man_id() == null || order.getDelivery_man_id().startsWith("d")) {
                throw new RuntimeException("No DeliveryMan available at the moment");
            }
            
            // removing products from warehouse
            
            String product_id = order.getProduct_id();
            String quantity = order.getQuantity();
            String warehouse_id = order.getWarehouse_id();
            
            WareHouse warehouse = wareHouseService.getWareHouseById(warehouse_id);
            
            for (int j = 0; j < warehouse.getProduct_ids().size(); j++) {
                if (warehouse.getProduct_ids().get(j).equals(product_id)) {
                    String q = warehouse.getQuantities().get(j);
                    int p_quantity = Integer.parseInt(q);
                    p_quantity -= Integer.parseInt(quantity);
                    warehouse.getQuantities().set(j, String.valueOf(p_quantity));
                }
            }
            wareHouseService.updateWareHouse(warehouse);

            
        } else if (status.equals("cancel")) {
            
            // making delivery man available again
            
            DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(deliveryman_id);
            
            deliveryMan.setStatus("available");
            
            deliveryManService.updateDeliveryMan(deliveryMan);
            
            if(order.getStatus() == "shipped")
            {
                // re-adding products to warehouse

                String product_id = order.getProduct_id();
                String quantity = order.getQuantity();
                String warehouse_id = order.getWarehouse_id();

                WareHouse warehouse = wareHouseService.getWareHouseById(warehouse_id);

                for (int j = 0; j < warehouse.getProduct_ids().size(); j++) {
                    if (warehouse.getProduct_ids().get(j).equals(product_id)) {
                        String q = warehouse.getQuantities().get(j);
                        int p_quantity = Integer.parseInt(q);
                        p_quantity += Integer.parseInt(quantity);
                        warehouse.getQuantities().set(j, String.valueOf(p_quantity));
                    }
                }

                wareHouseService.updateWareHouse(warehouse);
            }
            else if(order.getStatus() == "pending")
            {
                // do nothing
            }
            else if(order.getStatus() == "delivered")
            {
                throw new RuntimeException("Order already delivered");
            }
        }

        order.setStatus(status);
        orderService.updateOrder(order);
        return order;
    }
    
    // update order
    @PostMapping("/order/{id}")
    public Order updateOrder(@PathVariable("id") String id, @RequestBody OrderUpdateRequest data) {

        Order order = orderService.getOrderById(id);
        order.setCustomer_id(data.getCustomer_id());
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
        
        if(order.getPayment_method() == "online")
        {
            order.setTransaction_id(data.getTransaction_id());
        }

        return orderService.updateOrder(order);
        
    }
    
    @DeleteMapping("/order/{id}/delete")
    public void deleteOrder(@PathVariable String id)
    {

        if (orderService.getOrderById(id) == null)
        {
            throw new RuntimeException("Order Not Exists");
        }

        orderService.deleteOrder(id);

        if(orderService.getOrderById(id) != null)
        {
            throw new RuntimeException("Order Not deleted");
        }
    }
}
