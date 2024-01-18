package com.project.ims.Controllers;

// imports
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
import com.project.ims.Models.Order;
import com.project.ims.Models.ReturnOrder;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Requests.ReturnOrderAddRequest;
import com.project.ims.Requests.ReturnOrderUpdateRequest;
import com.project.ims.Services.ReturnOrderService;

@RestController
@RequestMapping("/api")
public class ReturnOrderController {

    // necessary dependency injections

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ReturnOrderService returnOrderService;

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
        returnOrder.setStatus("shipped");

        Order order = orderRepo.findById(data.getOrder_id()).orElse(null);
        returnOrder.setProduct_id(order.getProduct_id());
        returnOrder.setQuantity(order.getQuantity());
        returnOrder.setWarehouseId(order.getWarehouse_id());
        returnOrder.setRefund_amount(order.getTotal_amount());
        returnOrder.setOrder_id(order.getId());

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

        try{
            ReturnOrder returnOrder = returnOrderService.updateReturnOrderStatus(id, status);
            return returnOrder;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

    }

    // update return order

    @PostMapping("/return-order/{id}")
    public ReturnOrder updateReturnOrder(@PathVariable("id") String id, @RequestBody ReturnOrderUpdateRequest data) {

        ReturnOrder returnOrder = returnOrderService.getReturnOrderById(id);
        returnOrder.setPickup_address(data.getPickup_address());
        returnOrder.setReturn_reason(data.getReturn_reason());
        returnOrder.setStatus(data.getStatus());

        Order order = orderRepo.findById(returnOrder.getOrder_id()).orElse(null);
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
}
