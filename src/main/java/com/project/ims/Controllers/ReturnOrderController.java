package com.project.ims.Controllers;

// imports
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.Product;
import com.project.ims.Models.ReturnOrder;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Models.Supplier;
import com.project.ims.Requests.ReturnOrderAddRequest;
import com.project.ims.Requests.ReturnOrderUpdateRequest;
import com.project.ims.Services.DeliveryManService;
import com.project.ims.Services.OrderService;
import com.project.ims.Services.ProductService;
import com.project.ims.Services.RSOService;
import com.project.ims.Services.ReturnOrderService;
import com.project.ims.Services.SupplierService;

@RestController
@RequestMapping("/api")
public class ReturnOrderController {

    // necessary dependency injections
    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private RSOService returnSupplyOrderService;

    // Return Order APIs

    // get all return orders
    @GetMapping("/return-order")
    public List<ReturnOrder> getReturnOrders() {
        try{
            List<ReturnOrder> returnOrders = returnOrderService.getAllReturnOrder();
            return returnOrders;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // get return order by id
    @GetMapping("/return-order/{id}")
    public ReturnOrder getReturnOrderById(@PathVariable String id) {
        try{
            ReturnOrder returnOrder = returnOrderService.getReturnOrderById(id);
            return returnOrder;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // get return orders by customer id
    @GetMapping("/return-order/customer/{id}")
    public List<ReturnOrder> getReturnOrdersByCustomerId(@PathVariable String id) {
        try{
            List<ReturnOrder> returnOrders = returnOrderService.getAllReturnOrderByCustomerId(id);
            return returnOrders;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // create return order
    @PostMapping("/return-order")
    public ReturnOrder createReturnOrder(@RequestBody ReturnOrderAddRequest data) {

        String id = generateId();
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setId(id);
        returnOrder.setCustomerId(data.getCustomer_id());
        returnOrder.setPickup_address(data.getPickup_address());
        returnOrder.setReturn_reason(data.getReturn_reason());
        returnOrder.setStatus("pending");

        // set date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        returnOrder.setDate_time(formattedDateTime);

        Order order = orderService.getOrderById(data.getOrder_id());

        returnOrder.setProduct_id(order.getProduct_id());
        returnOrder.setQuantity(order.getQuantity());
        returnOrder.setWarehouseId(order.getWarehouse_id());
        returnOrder.setRefund_amount(order.getTotal_amount());
        returnOrder.setOrder_id(order.getId());

        List<DeliveryMan> deliveryMans = deliveryManService.getAllDeliveryManByWarehouse(order.getWarehouse_id());

        for(DeliveryMan i : deliveryMans)
        {
            if (i.getStatus().equals("available")) {
                i.setStatus("unavailable");
                try {
                    deliveryManService.updateDeliveryMan(i);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
                returnOrder.setDelivery_man_id(i.getId());
            }
        }
        
        if(returnOrder.getDelivery_man_id() == null)
        {
            System.out.println("No deliveryman available");
            return null;
        }

        try{
            returnOrderService.addReturnOrder(returnOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return returnOrder;
    }

    // update return order status
    @PostMapping("/return-order/{id}/status")
    public ReturnOrder updateReturnOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status) {

        ReturnOrder returnOrder = returnOrderService.getReturnOrderById(id);

        returnOrder.setStatus(status);

        if (status.equals("approved")) {
            Order order = orderService.getOrderById(returnOrder.getOrder_id());
            order.setStatus("returned");
            
            try{
                orderService.updateOrder(order);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }

        } else if (status.equals("rejected")) {

            Order order = orderService.getOrderById(returnOrder.getOrder_id());
            order.setStatus("delivered");
            
            try{
                orderService.updateOrder(order);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }

            DeliveryMan m = deliveryManService.getDeliveryManById(returnOrder.getDelivery_man_id());

            m.setStatus("available");

            try{
                deliveryManService.updateDeliveryMan(m);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }

        } else if (status.equals("arrived")) {
            
            ReturnSupplyOrder returnSupplyOrder = createRSO(returnOrder);
            
            try{
                returnSupplyOrderService.addReturnSupplyOrder(returnSupplyOrder);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }

        }

        try{
            returnOrderService.updateReturnOrder(returnOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return returnOrder;
    }

    // update return order

    @PostMapping("/return-order/{id}")
    public ReturnOrder updateReturnOrder(@PathVariable("id") String id, @RequestBody ReturnOrderUpdateRequest data) {

        ReturnOrder returnOrder = returnOrderService.getReturnOrderById(id);
        returnOrder.setPickup_address(data.getPickup_address());
        returnOrder.setReturn_reason(data.getReturn_reason());
        returnOrder.setStatus(data.getStatus());

        Order order = orderService.getOrderById(returnOrder.getOrder_id());
        returnOrder.setProduct_id(order.getProduct_id());
        returnOrder.setQuantity(order.getQuantity());
        returnOrder.setWarehouseId(order.getWarehouse_id());
        returnOrder.setRefund_amount(order.getTotal_amount());
        returnOrder.setOrder_id(order.getId());
        returnOrder.setCustomerId(order.getCustomerId());

        returnOrder.setDate_time(data.getDate_time());
        returnOrder.setDelivered_date_time(data.getDelivered_date_time());
        returnOrder.setDelivery_man_id(data.getDelivery_man_id());

        try{
            returnOrderService.updateReturnOrder(returnOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return returnOrder;
    }

    // get return orders by warehouse id
    @GetMapping("/return-orders/warehouse/{id}")
    public List<ReturnOrder> getReturnOrdersByWarehouseId(@PathVariable String id) {
        try{
            List<ReturnOrder> returnOrders = returnOrderService.findByWarehouseId(id);
            return returnOrders;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // delete return order

    @DeleteMapping("/return-order/{id}")
    public void deleteReturnOrder(@PathVariable("id") String id) {
        try{
            returnOrderService.deleteReturnOrder(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public String generateId() {
        Random rand = new Random();
        String id = "r" + rand.nextInt(1000000);
        return id;
    }

    public ReturnSupplyOrder createRSO(ReturnOrder returnOrder)
    {
        ReturnSupplyOrder returnSupplyOrder = new ReturnSupplyOrder();
        Random rand = new Random();
        String returnSupplyOrderId = "rso" + rand.nextInt(1000000);
        returnSupplyOrder.setId(returnSupplyOrderId);

        // set date and time when returned order arrives at warehouse
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        returnSupplyOrder.setDate_time(formattedDateTime);

        Order order = orderService.getOrderById(returnOrder.getOrder_id());
        returnSupplyOrder.setOrder_id(order.getId());
        returnSupplyOrder.setProduct_id(order.getProduct_id());
        returnSupplyOrder.setQuantity(order.getQuantity());
        returnSupplyOrder.setWarehouse_id(order.getWarehouse_id());
        returnSupplyOrder.setRefund_amount(order.getTotal_amount());
        returnSupplyOrder.setStatus("pending");
        returnSupplyOrder.setReturn_reason(returnOrder.getReturn_reason());

        Product product = productService.getProductById(order.getProduct_id());
        returnSupplyOrder.setSupplier_id(product.getSupplier_id());

        Supplier supplier = supplierService.getSupplierById(product.getSupplier_id());
        returnSupplyOrder.setDelivery_address(supplier.getAddress());

        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(order.getWarehouse_id());
        
        for (DeliveryMan d : deliveryMen) {
            if (d.getStatus() == "available") {
                d.setStatus("unavailable");
                returnSupplyOrder.setDelivery_man_id(d.getId());

                try {
                    deliveryManService.updateDeliveryMan(d);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }

                break;
            }
        }

        return returnSupplyOrder;
    }
}
