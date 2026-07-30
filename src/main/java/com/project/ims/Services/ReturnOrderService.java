package com.project.ims.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(ReturnOrderService.class);

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
        returnOrder.setDateTime(formattedDateTime);

        // assign deliveryMan to return order
        String deliveryManId = assignDeliveryMan(returnOrder);

        if (deliveryManId == null) {
            returnOrder.setStatus("pending");
        }

        returnOrder.setDeliveryManId(deliveryManId);

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
                    logger.error(e.getMessage(), e);
                    return null;
                }
                return i.getId();
            }
        }

        logger.debug("No deliveryman available");
        return null;
    }
    
    @Override
    public ReturnOrder updateReturnOrderStatus(String id, String status) {

        ReturnOrder returnOrder = getReturnOrderById(id);
        Order order = orderRepo.findById(returnOrder.getOrderId()).orElse(null);

        returnOrder.setStatus(status);
      

        if (status.equals("approved")) {
            order.setStatus("returned");

            try {
                orderRepo.save(order);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }

            createRSO(returnOrder);

        } else if (status.equals("rejected")) {
            order.setStatus("delivered");

            try {
                orderRepo.save(order);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }

            DeliveryMan m = deliveryManService.getDeliveryManById(returnOrder.getDeliveryManId());

            m.setStatus("available");

            try {
                deliveryManService.updateDeliveryMan(m);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
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

        
        Order order = orderRepo.findById(returnOrder.getOrderId()).orElse(null);
        
        if (order == null) {
            logger.debug("Order not found");
            return null;
        }

        returnSupplyOrder.setOrderId(order.getId());
        returnSupplyOrder.setWarehouseId(order.getWarehouseId());
        returnSupplyOrder.setProductId(order.getProductId());
        returnSupplyOrder.setQuantity(order.getQuantity());
        returnSupplyOrder.setRefundAmount(order.getTotalAmount());

        Product product = productService.getProductById(order.getProductId());

        Supplier supplier = supplierService.getSupplierById(product.getSupplierId());

        returnSupplyOrder.setDeliveryAddress(supplier.getAddress());
        returnSupplyOrder.setReturnReason(returnOrder.getReturnReason());
        returnSupplyOrder.setStatus("shipped");
        returnSupplyOrder.setSupplierId(product.getSupplierId());
        returnSupplyOrder.setDeliveryManId(order.getDeliveryManId());

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

    public List<Map<String, Object>> getReturnOrdersByRbyDid(String id){
         if (id.equals("")) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!deliveryManRepo.existsById(id)) {
            throw new RuntimeException("DeliveryMan  with id " + id + " does not exist");
        }

        List<Order> orders = orderRepo.findAll();
        List<Map<String, Object>> statusCorder = new ArrayList<>();

        for (Order o : orders) {
            if (o.getStatus().equals("returned") && o.getDeliveryManId().equals(id)) {

                User user = userService.getUserByUserId(o.getCustomerId());
                Customer customer = customerService.getCustomerById(o.getCustomerId());
                WareHouse wareHouse = wareHouseService.getWareHouseById(o.getWarehouseId());
                Product product = productService.getProductById(o.getProductId());
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
            logger.debug("empty id/s");
            return null;
        }
        ReturnOrder o = getReturnOrderById(ordId);
        if(o == null)
        {
            logger.debug("no order of following order id");
            return null;
        }
        DeliveryMan d = deliveryManService.getDeliveryManById(id);

        if( o.getStatus().equals("pending") && d.getStatus().equals("available")){
            o.setDeliveryManId(id);
            o.setStatus("shipped");
            d.setStatus("unavailable");
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            o.setDeliveredDateTime(formattedDateTime);
            deliveryManService.updateDeliveryMan(d);
           updateReturnOrder(o);
        }
        else{
            logger.debug("either order status is not pending or delivery man is not free");
            return null;
        }
        return o;
    }
    public List<Map<String,Object>> orderStatusP (String id){
        if(id.equals("")){
            logger.debug("id is empty");
            return null;
        }

        List<ReturnOrder> ro = getAllReturnOrder();
        List<Map<String,Object>> returnord = new ArrayList<>();
        DeliveryMan d = deliveryManService.getDeliveryManById(id);
        WareHouse wareHouse = wareHouseService.getWareHouseById(d.getWarehouseId());
        if(d==null || wareHouse== null){
            logger.debug("either deliveryman is not exist or ware house is not exist");
            return null;
        }
        for(ReturnOrder o : ro){
            if(o.getStatus().equals("pending") && o.getWarehouseId().equals(wareHouse.getId())){
                Product p = productService.getProductById(o.getProductId());
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
            logger.debug("Delivery man not exists");
            return null;
        }
        WareHouse wareHouse = wareHouseService.getWareHouseById( deliveryMan.getWarehouseId());
        if(wareHouse == null)
        {
            logger.debug("delivery man warehouse donot exists");
            return null;
        }
        List<ReturnOrder> orders = returnOrderRepo.findAll();
        Map<String, Object> Filterorders = new HashMap<>();

        for (ReturnOrder o : orders) {
            if (o.getStatus().equals("shipped") && o.getDeliveryManId().equals(id)) {                
                Customer customer = customerService.getCustomerById(o.getCustomerId());
                User user = userService.getUserByUserId(o.getCustomerId());
                Product product = productService.getProductById(o.getProductId());
                   
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
