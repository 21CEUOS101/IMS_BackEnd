package com.project.ims.Controllers;

// imports
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.format.DateTimeFormatter;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.W2WOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Requests.OrderAddRequest;
import com.project.ims.Requests.OrderUpdateRequest;
import com.project.ims.Services.DeliveryManService;
import com.project.ims.Services.OrderService;
import com.project.ims.Services.ProductService;
import com.project.ims.Services.W2WOrderService;
import com.project.ims.Services.WareHouseService;
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

    // necessary dependency injections
    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private W2WOrderService w2wOrderService;


    // Order APIs


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
            throw new RuntimeException("Invalid Request");
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

        Order order = orderService.getOrderById(id);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        
        if (status.equals("delivered")) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            order.setDelivered_date_time(formattedDateTime);
            
            DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(order.getDelivery_man_id());
            
            deliveryMan.setStatus("available");
            
            deliveryManService.updateDeliveryMan(deliveryMan);

        } else if (status.equals("shipped")) {
            
            // assigning delivery man to order
            
            String deliveryMan = assignDeliveryMan(order);

            if (deliveryMan == null || !deliveryMan.startsWith("d")) {
                throw new RuntimeException("No DeliveryMan available at the moment");
            }

            order.setDelivery_man_id(deliveryMan);

            // ------------------------------------------------------------------------
            
            // removing products from warehouse
            
            String product_id = order.getProduct_id();
            String quantity = order.getQuantity();
            String warehouse_id = order.getWarehouse_id();
            
            WareHouse warehouse = wareHouseService.getWareHouseById(warehouse_id);
            
            for (int j = 0; j < warehouse.getProduct_ids().size(); j++) {
                if (warehouse.getProduct_ids().get(j).equals(product_id)) {
                    String q = warehouse.getQuantities().get(j);
                    int p_quantity = Integer.parseInt(q);

                    if(p_quantity < Integer.parseInt(quantity))
                    {
                        System.out.println("Product not available in warehouse");
                        return null;
                    }

                    p_quantity -= Integer.parseInt(quantity);
                    warehouse.getQuantities().set(j, String.valueOf(p_quantity));
                }
            }
            wareHouseService.updateWareHouse(warehouse);

            // ------------------------------------------------------------------------

            
        } else if (status.equals("cancel")) {
            
            // making delivery man available again
            
            DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(order.getDelivery_man_id());
            
            deliveryMan.setStatus("available");
            
            deliveryManService.updateDeliveryMan(deliveryMan);

            // ------------------------------------------------------------------------
            
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
    
    public String generateOrderId()
    {
        Random rand = new Random();
        int random = rand.nextInt(1000000);
        String id = 'o' + String.valueOf(random);
        return id;
    }

    // check in warehouse stock if product is available or not
    public int checkStock(String product_id, String quantity, String warehouse_id) {
        WareHouse warehouse = wareHouseService.getWareHouseById(warehouse_id);

        for (int j = 0; j < warehouse.getProduct_ids().size(); j++) {
            if (warehouse.getProduct_ids().get(j).equals(product_id)) {
                int q1 = Integer.parseInt(warehouse.getQuantities().get(j));
                int q2 = Integer.parseInt(quantity);

                if (q1 < q2) {
                    return q1;
                }
                break;
            }
        }

        return -1;
    }
    
    // if stock is not available then create warehouse to warehouse order if product is available in other warehouse
    public void handleStock(String product_id , String quantity , String r_warehouse_id )
    {

        int available_quantity = checkStock(product_id, quantity, r_warehouse_id);

        List<WareHouse> warehouses = wareHouseService.getAllWareHouse();

        int needed_quantity = Integer.parseInt(quantity) - available_quantity;

        for (int i = 0; i < warehouses.size(); i++) {

            if (warehouses.get(i).getId().equals(r_warehouse_id)) {
                continue;
            }

            List<String> product_ids = warehouses.get(i).getProduct_ids();
            List<String> quantities = warehouses.get(i).getQuantities();

            for (int j = 0; j < product_ids.size(); j++) {
                if (product_ids.get(j).equals(product_id)) {
                    if (needed_quantity <= Integer.parseInt(quantities.get(j))) {
                        // create warehouse to warehouse order
                        createW2WOrder(product_id , Integer.toString(needed_quantity) , r_warehouse_id , warehouses.get(i).getId());
                        return;
                    }
                }
            }
        }

        throw new RuntimeException("Product not available in any warehouse");
    }
    
    public void createW2WOrder(String product_id , String quantity , String r_warehouse_id , String s_warehouse_id) {
        
        W2WOrder w2wOrder = new W2WOrder();
        Random rand = new Random();
        String id = "w2w" + rand.nextInt(1000000);
        w2wOrder.setId(id);
        w2wOrder.setProduct_id(product_id);
        w2wOrder.setQuantity(quantity);
        w2wOrder.setR_warehouse_id(r_warehouse_id);
        w2wOrder.setS_warehouse_id(s_warehouse_id);
        w2wOrder.setStatus("pending");

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        w2wOrder.setDate_time(formattedDateTime);

        try{
            w2wOrderService.addW2WOrder(w2wOrder);
        }
        catch(Exception e){
            System.out.println(e);
        }
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

        // checking if warehouse has the product or not otherwise create warehouse to warehouse order
        int available_quantity = checkStock(product_id, quantity, warehouse_id);
        if (available_quantity >= 0) {
            handleStock(product_id , quantity, warehouse_id);
        }

        // set warehouse id
        order.setWarehouse_id(warehouse_id);

        orders.add(order);

        orderService.addOrder(order);
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
