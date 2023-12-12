package com.project.ims.Controllers;

import java.util.List;
import java.util.Random;

import javax.management.RuntimeErrorException;

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
import com.project.ims.Requests.OrderAddRequest;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class OrderController {
    
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

    @GetMapping("/order/{id}")
    public Order getOrderById(@PathVariable("id") String id) {

        Order order = orderService.getOrderById(id);

        return order;
    }
    
    @PostMapping("/order")
    public Order createOrder(@RequestBody OrderAddRequest data) {

        Order order = new Order();

        Random rand = new Random();
        String id = 'o' + String.valueOf(rand.nextInt(1000000));
        order.setId(id);

        order.setProduct_ids(data.getProduct_ids());
        order.setSource_id(data.getSource_id());
        order.setDestination_id(data.getDestination_id());
        order.setTotal_amount(data.getTotal_amount());
        order.setStatus("pending");
        order.setDate_time(data.getDate_time());
        order.setPayment_method(data.getPayment_method());

        if (order.getPayment_method() == "online" && data.getTransaction_id() != null) {
            order.setTransaction_id(data.getTransaction_id());
        } else if (order.getPayment_method() == "online" && data.getTransaction_id() == null) {
            throw new RuntimeException("Transaction ID is required for online payment");
        }

        order.setDelivery_address(data.getDelivery_address());

        return new Order();
    }
    
    @PostMapping("/order/{id}/status")
    public Order updateOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status , @RequestParam("deliveryman_id") String deliveryman_id) {

        Order order = orderService.getOrderById(id);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        order.setStatus(status);

        if (status.equals("delivered")) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            order.setDelivered_date_time(formattedDateTime);

            DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(deliveryman_id);

            deliveryMan.setStatus("available");

            deliveryManService.updateDeliveryMan(deliveryMan);
        }
        else if (status.equals("shipped") && order.getSource_id().startsWith("w"))
        {
            List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(order.getSource_id());

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
        }
        else if (status.equals("cancel"))
        {
            DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(deliveryman_id);

            deliveryMan.setStatus("available");

            deliveryManService.updateDeliveryMan(deliveryMan);

            
        }

        return order;
    }
    
    

}
