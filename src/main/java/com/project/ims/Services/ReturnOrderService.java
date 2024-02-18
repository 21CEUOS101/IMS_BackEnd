package com.project.ims.Services;

// imports
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IReturnOrderService;
import com.project.ims.Models.Customer;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.Product;
import com.project.ims.Models.ReturnOrder;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Models.Supplier;
import com.project.ims.Models.User;
import com.project.ims.Models.WareHouse;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ReturnOrderRepo;
import com.project.ims.Repo.WareHouseRepo;

@Service
public class ReturnOrderService implements IReturnOrderService {

    // necessary dependency Injections
    @Autowired
    private WareHouseRepo wareHouseRepo;

    @Autowired
    private ReturnOrderRepo returnOrderRepo;

    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private RSOService returnSupplyOrderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private DeliveryManRepo deliveryManRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private OrderService orderService;


    // Services

    @Override
    public List<ReturnOrder> getAllReturnOrder() {
        return returnOrderRepo.findAll();
    }

    @Override
    public ReturnOrder getReturnOrderById(String id) {
        return returnOrderRepo.findById(id).orElse(null);
    }

    @Override
    public ReturnOrder addReturnOrder(ReturnOrder returnOrder) {

        if (returnOrder.getId() == null || returnOrder.getId().isEmpty())
        {
            throw new RuntimeException("Return Order ID cannot be empty");
        }
        else if (returnOrderRepo.existsById(returnOrder.getId()))
        {
            throw new RuntimeException("Return Order ID already exists");
        }
        else if (returnOrder.getId().charAt(0) != 'r')
        {
            throw new RuntimeException("Return Order ID must start with 'r'");
        }

        // set date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        returnOrder.setDate_time(formattedDateTime);

        // assign deliveryMan to return order
        String deliveryManId = assignDeliveryMan(returnOrder);

        if (deliveryManId == null) {
            returnOrder.setStatus("pending");
        }

        returnOrder.setDelivery_man_id(deliveryManId);

        return returnOrderRepo.save(returnOrder);
    }

    @Override
    public ReturnOrder updateReturnOrder(ReturnOrder returnOrder) {
        return returnOrderRepo.save(returnOrder);
    }

    @Override
    public void deleteReturnOrder(String id) {
        if (!returnOrderRepo.existsById(id))
        {
            throw new RuntimeException("Return Order ID does not exist");
        }
        returnOrderRepo.deleteById(id);
    }

    @Override
    public List<ReturnOrder> getAllReturnOrderByCustomerId(String id) {
        return returnOrderRepo.findAllByCustomerId(id);
    }

    public List<ReturnOrder> findByWarehouseId(String id) {

        if (id == null || id.isEmpty()) {
            throw new RuntimeException("Warehouse ID cannot be empty");
        } else if (!wareHouseRepo.existsById(id)) {
            throw new RuntimeException("Warehouse ID does not exist");
        }

        return returnOrderRepo.findAllByWarehouseId(id);
    }
    
    public String assignDeliveryMan(ReturnOrder returnOrder)
    {
        List<DeliveryMan> deliveryMans = deliveryManService.getAllDeliveryManByWarehouse(returnOrder.getWarehouseId());

        for (DeliveryMan i : deliveryMans) {
            if (i.getStatus().equals("available")) {
                i.setStatus("unavailable");
                try {
                    deliveryManService.updateDeliveryMan(i);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
                return i.getId();
            }
        }

        System.out.println("No deliveryman available");
        return null;
    }
    
    @Override
    public ReturnOrder updateReturnOrderStatus(String id, String status) {

        ReturnOrder returnOrder = getReturnOrderById(id);
        Order order = orderRepo.findById(returnOrder.getOrder_id()).orElse(null);

        returnOrder.setStatus(status);
      

        if (status.equals("approved")) {
            order.setStatus("returned");

            try {
                orderRepo.save(order);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }

            createRSO(returnOrder);

        } else if (status.equals("rejected")) {
            order.setStatus("delivered");

            try {
                orderRepo.save(order);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }

            DeliveryMan m = deliveryManService.getDeliveryManById(returnOrder.getDelivery_man_id());

            m.setStatus("available");

            try {
                deliveryManService.updateDeliveryMan(m);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }

        }

        return returnOrderRepo.save(returnOrder);
    }
    
    public ReturnSupplyOrder createRSO(ReturnOrder returnOrder)
    {
        ReturnSupplyOrder returnSupplyOrder = new ReturnSupplyOrder();
        Random rand = new Random();
        String returnSupplyOrderId = "rso" + rand.nextInt(1000000);
        returnSupplyOrder.setId(returnSupplyOrderId);

        
        Order order = orderRepo.findById(returnOrder.getOrder_id()).orElse(null);
        
        if (order == null) {
            System.out.println("Order not found");
            return null;
        }

        returnSupplyOrder.setOrder_id(order.getId());
        returnSupplyOrder.setWarehouse_id(order.getWarehouse_id());
        returnSupplyOrder.setProduct_id(order.getProduct_id());
        returnSupplyOrder.setQuantity(order.getQuantity());
        returnSupplyOrder.setRefund_amount(order.getTotal_amount());

        Product product = productService.getProductById(order.getProduct_id());

        Supplier supplier = supplierService.getSupplierById(product.getSupplierId());

        returnSupplyOrder.setDelivery_address(supplier.getAddress());
        returnSupplyOrder.setReturn_reason(returnOrder.getReturn_reason());
        returnSupplyOrder.setStatus("shipped");
        returnSupplyOrder.setSupplier_id(product.getSupplierId());
        returnSupplyOrder.setDelivery_man_id(order.getDelivery_man_id());

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

    public List<Map<String, Object>> getReturnOrdersByRbyDid(String id){
         if (id.equals("")) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!deliveryManRepo.existsById(id)) {
            throw new RuntimeException("DeliveryMan  with id " + id + " does not exist");
        }

        List<Order> orders = orderRepo.findAll();
        List<Map<String, Object>> statusCorder = new ArrayList<>();

        for (Order o : orders) {
            if (o.getStatus().equals("returned") && o.getDelivery_man_id().equals(id)) {

                User user = userService.getUserByUserId(o.getCustomerId());
                Customer customer = customerService.getCustomerById(o.getCustomerId());
                WareHouse wareHouse = wareHouseService.getWareHouseById(o.getWarehouse_id());
                Product product = productService.getProductById(o.getProduct_id());
                // System.out.println(user);
                if (user != null) {

                    Map<String, Object> orderWithCustomer = new HashMap<>();
                    orderWithCustomer.put("order", o);
                    orderWithCustomer.put("user", user);
                    orderWithCustomer.put("customer", customer);
                    orderWithCustomer.put("warehouse", wareHouse);
                    orderWithCustomer.put("product",product);
                    statusCorder.add(orderWithCustomer);
                }

            }
        }
        return statusCorder;
    }

    public ReturnOrder updateOrderStatusSByDid(String ordId, String id) {
        if(ordId.equals("")  ||  id.equals("")){
            System.out.println("empty id/s");
            return null;
        }
        ReturnOrder o = getReturnOrderById(ordId);
        if(o == null)
        {
            System.out.println("no order of following order id");
            return null;
        }
        DeliveryMan d = deliveryManService.getDeliveryManById(id);

        if( o.getStatus().equals("pending") && d.getStatus().equals("available")){
            o.setDelivery_man_id(id);
            o.setStatus("shipped");
            d.setStatus("unavailable");
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            o.setDelivered_date_time(formattedDateTime);
            deliveryManService.updateDeliveryMan(d);
           updateReturnOrder(o);
        }
        else{
            System.out.println("either order status is not pending or delivery man is not free");
            return null;
        }
        return o;
    }
    public List<Map<String,Object>> orderStatusP (String id){
        if(id.equals("")){
            System.out.println("id is empty");
            return null;
        }

        List<ReturnOrder> ro = getAllReturnOrder();
        List<Map<String,Object>> returnord = new ArrayList<>();
        DeliveryMan d = deliveryManService.getDeliveryManById(id);
        WareHouse wareHouse = wareHouseService.getWareHouseById(d.getWarehouseId());
        if(d==null || wareHouse== null){
            System.out.println("either deliveryman is not exist or ware house is not exist");
            return null;
        }
        for(ReturnOrder o : ro){
            if(o.getStatus().equals("pending") && o.getWarehouseId().equals(wareHouse.getId())){
                Product p = productService.getProductById(o.getProduct_id());
                User user = userService.getUserByUserId(o.getCustomerId());
                Customer cust = customerService.getCustomerById(o.getCustomerId());
                Map<String, Object> orderWithCustomer = new HashMap<>();
                orderWithCustomer.put("returnorder", o);                   
                orderWithCustomer.put("customer", cust);
                orderWithCustomer.put("warehouse", wareHouse);
                orderWithCustomer.put("product",p);
                orderWithCustomer.put("user",user);
                returnord.add(orderWithCustomer);
            }
        }
        return returnord;
    }

    public Map<String, Object> orderStatusS(String id) {

        // Checking if the DelivereyMan data is null or not
        if (id.equals("")) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!deliveryManRepo.existsById(id)) {
            throw new RuntimeException("DeliveryMan  with id " + id + " does not exist");
        }
        DeliveryMan deliveryMan =  deliveryManService.getDeliveryManById(id);
        if(deliveryMan == null)
        {
            System.out.println("Delivery man not exists");
            return null;
        }
        WareHouse wareHouse = wareHouseService.getWareHouseById( deliveryMan.getWarehouseId());
        if(wareHouse == null)
        {
            System.out.println("delivery man warehouse donot exists");
            return null;
        }
        List<ReturnOrder> orders = returnOrderRepo.findAll();
        Map<String, Object> Filterorders = new HashMap<>();

        for (ReturnOrder o : orders) {
            if (o.getStatus().equals("shipped") && o.getDelivery_man_id().equals(id)) {                
                Customer customer = customerService.getCustomerById(o.getCustomerId());
                User user = userService.getUserByUserId(o.getCustomerId());
                Product product = productService.getProductById(o.getProduct_id());
                   
                Filterorders.put("returnorder", o);                   
                Filterorders.put("customer", customer);
                Filterorders.put("warehouse", wareHouse);
                Filterorders.put("product",product);
                Filterorders.put("user",user);
                break;
                
            }
        }
        return Filterorders;

    }
    

}
