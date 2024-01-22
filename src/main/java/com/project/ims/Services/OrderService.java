package com.project.ims.Services;

// imports
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IOrderService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.GlobalProducts;
import com.project.ims.Models.Order;
import com.project.ims.Models.W2WOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Repo.GlobalProductsRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;

@Service
public class OrderService implements IOrderService {

    // necessary dependency Injections
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private GlobalProductsRepo globalProductsRepo;

    @Autowired
    private W2WOrderService w2wOrderService;

    // Services

    @Override
    public Order getOrderById(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

        return orderRepo.findById(id).orElse(null);
    }

    @Override
    public Order addOrder(Order order) {
        
        // Checking if the order ID is valid
        if(order.getId() == null || order.getId().isEmpty())
        {
            throw new RuntimeException("Order ID cannot be empty");
        }
        else if(orderRepo.existsById(order.getId()))
        {
            throw new RuntimeException("Order ID already exists");
        }
        else if (order.getId().charAt(0) != 'o')
        {
            throw new RuntimeException("Order ID must start with 'o'");
        }

        String warehouse_id = order.getWarehouse_id();

        // set time and date
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        order.setDate_time(formattedDateTime);
        
        // set total amount
        Integer totalPrice = 0;
        String product_id = order.getProduct_id();
        String quantity = order.getQuantity();
        Integer price = Integer.parseInt(productRepo.findById(product_id).get().getPrice());
        Integer q = Integer.parseInt(quantity);
        totalPrice += price * q;
        order.setTotal_amount(totalPrice.toString());

        // checking if warehouse has the product or not otherwise create warehouse to warehouse order
        
        if (!checkStock(product_id, quantity, warehouse_id)) {
            handleStock(product_id, quantity, warehouse_id , order.getId());
        }
        else {
            // removing products from warehouse
            WareHouse warehouse = wareHouseService.getWareHouseById(warehouse_id);
            for (int j = 0; j < warehouse.getProduct_ids().size(); j++) {
                if (warehouse.getProduct_ids().get(j).equals(product_id)) {
                    String q1 = warehouse.getQuantities().get(j);
                    int p_quantity = Integer.parseInt(q1);
                    p_quantity -= Integer.parseInt(quantity);
                    warehouse.getQuantities().set(j, String.valueOf(p_quantity));
                }
            }

            order.setStatus("shipped");

            // assigning deliveryman to order if product is available in warehouse

            // if no deliveryman is available then set status to pending

            String deliveryMan = assignDeliveryMan(order);

            if (deliveryMan == null) {
                order.setStatus("pending");
            }

            order.setDelivery_man_id(deliveryMan);
            
            try{
                wareHouseService.updateWareHouse(warehouse);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }

        return orderRepo.save(order);
    }

    @Override
    public Order updateOrder(Order order) {
        
        // Checking if the order data is null or not
        if(order == null)
        {
            throw new RuntimeException("Order data shouldn't be null");
        }

        return orderRepo.save(order);
    }

    @Override
    public Order updateOrderStatus(String id , String status)
    {
        Order order = getOrderById(id);

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

        }
        else if (status.equals("cancel")) {



            // ------------------------------------------------------------------------

            if (order.getStatus().equals("shipped")) {
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

                // making delivery man available again

                DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(order.getDelivery_man_id());

                deliveryMan.setStatus("available");

                deliveryManService.updateDeliveryMan(deliveryMan);

            } else if (order.getStatus().equals("pending")) {
                // all active w2w orders with this order id should be cancelled

                List<W2WOrder> w2wOrders = w2wOrderService.getAllW2WOrderByOrderId(order.getId());

                for (W2WOrder w2wOrder : w2wOrders) {
                    w2wOrderService.updateW2WOrderStatus(w2wOrder.getId(), status);
                }

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
            } else if (order.getStatus().equals("delivered")) {
                throw new RuntimeException("Order already delivered");
            }
        }
        else if (status.equals("shipped"))
        {
            // checking if w2w orders are delivered or not

            List<W2WOrder> w2wOrders = w2wOrderService.getAllW2WOrderByOrderId(order.getId());

            for (W2WOrder w2wOrder : w2wOrders) {
                if (!w2wOrder.getStatus().equals("delivered")) {
                    throw new RuntimeException("W2W orders are not delivered yet");
                }
            }

            // assigning deliveryman to order

            String deliveryMan = assignDeliveryMan(order);
            order.setDelivery_man_id(deliveryMan);
            if (deliveryMan == null) {
                order.setStatus("pending");
            }
        }

        order.setStatus(status);
        return orderRepo.save(order);

    }

    @Override
    public void deleteOrder(String id) {

        // Checking if the order ID is valid or not
        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }
        else if(!orderRepo.existsById(id))
        {
            throw new RuntimeException("Order with id " + id + " does not exist");
        }

        orderRepo.deleteById(id);
    }

    public List<Order> getAllOrder() {
        return orderRepo.findAll();
    }

    @Override
    public List<Order> getAllOrderByCustomerId(String id) {
        return orderRepo.findByCustomerId(id);
    }

    // check in warehouse stock if product is available or not
    public boolean checkStock(String product_id, String quantity, String warehouse_id) {
        
        GlobalProducts gp = globalProductsRepo.findById(product_id).orElse(null);

        if (gp == null) {
            throw new RuntimeException("Product not found");
        }

        List<String> warehouses = gp.getWarehouses();
        List<String> quantities = gp.getQuantities();

        if (warehouses == null) {
            throw new RuntimeException("Product not found");
        }

        if (!warehouses.contains(warehouse_id)) {
            throw new RuntimeException("Product not found");
        }

        int available_quantity = quantities.get(warehouses.indexOf(warehouse_id)).equals("null") ? 0
                : Integer.parseInt(quantities.get(warehouses.indexOf(warehouse_id)));

        if (available_quantity >= Integer.parseInt(quantity)) {
            return true;
        }

        return false;
    }
    
    // if stock is not available then create warehouse to warehouse order if product is available in other warehouse
    public void handleStock(String product_id , String quantity , String r_warehouse_id , String orderId)
    {

        GlobalProducts gp = globalProductsRepo.findById(product_id).orElse(null);

        if (gp == null) {
            throw new RuntimeException("Product not found");
        }

        List<String> warehouses = gp.getWarehouses();
        List<String> quantities = gp.getQuantities();

        if (warehouses == null) {
            throw new RuntimeException("Product not found");
        }

        if (!warehouses.contains(r_warehouse_id)) {
            throw new RuntimeException("Product not found");
        }

        int available_quantity = quantities.get(warehouses.indexOf(r_warehouse_id)).equals("null") ? 0
                : Integer.parseInt(quantities.get(warehouses.indexOf(r_warehouse_id)));

        int needed_quantity = Integer.parseInt(quantity) - available_quantity;

        // checking if product is available in other warehouses or not

        // checking which warehouses has highest quantity of that product then create w2w order

        while (needed_quantity > 0) {
            int max = 0;
            String max_warehouse_id = null;
            for (String key : warehouses) {

                int quantity1 = quantities.get(warehouses.indexOf(key)).equals("null") ? 0
                        : Integer.parseInt(quantities.get(warehouses.indexOf(key)));

                if (quantity1 > max && !key.equals(r_warehouse_id)) {
                    max = quantity1;
                    max_warehouse_id = key;
                }
            }

            if (max_warehouse_id == null) {
                throw new RuntimeException("Product not available in any warehouse");
            }

            if (needed_quantity - max < 0) {
                createW2WOrder(product_id, String.valueOf(needed_quantity), r_warehouse_id, max_warehouse_id, orderId);
                needed_quantity = 0;
                break;
            }
            
            needed_quantity -= max;

            createW2WOrder(product_id, String.valueOf(max), r_warehouse_id, max_warehouse_id, orderId);

        }

        // removing products from warehouse

        WareHouse warehouse = wareHouseService.getWareHouseById(r_warehouse_id);

        for (int j = 0; j < warehouse.getProduct_ids().size(); j++) {
            if (warehouse.getProduct_ids().get(j).equals(product_id)) {
                String q = warehouse.getQuantities().get(j);
                int p_quantity = Integer.parseInt(q);
                p_quantity -= available_quantity;
                warehouse.getQuantities().set(j, String.valueOf(p_quantity));
            }
        }

        try {
            wareHouseService.updateWareHouse(warehouse);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void createW2WOrder(String product_id, String quantity, String r_warehouse_id, String s_warehouse_id,
            String orderId) {

        W2WOrder w2wOrder = new W2WOrder();
        Random rand = new Random();
        String id = "w2w" + rand.nextInt(1000000);
        w2wOrder.setId(id);
        w2wOrder.setProduct_id(product_id);
        w2wOrder.setQuantity(quantity);
        w2wOrder.setR_warehouse_id(r_warehouse_id);
        w2wOrder.setS_warehouse_id(s_warehouse_id);
        w2wOrder.setOrderId(orderId);
        w2wOrder.setStatus("shipped");

        try {
            w2wOrderService.addW2WOrder(w2wOrder);
        } catch (Exception e) {
            System.out.println(e);
        }
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
