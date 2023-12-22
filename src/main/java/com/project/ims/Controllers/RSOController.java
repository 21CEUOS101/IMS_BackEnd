package com.project.ims.Controllers;
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
import org.springframework.web.bind.annotation.RestController;

import com.project.ims.IServices.IAdminService;
import com.project.ims.IServices.ICustomerService;
import com.project.ims.IServices.IDeliveryManService;
import com.project.ims.IServices.IOrderService;
import com.project.ims.IServices.IProductService;
import com.project.ims.IServices.IRSOService;
import com.project.ims.IServices.IReturnOrderService;
import com.project.ims.IServices.ISupplierService;
import com.project.ims.IServices.ISupplyOrderService;
import com.project.ims.IServices.IW2WOrderService;
import com.project.ims.IServices.IWManagerService;
import com.project.ims.IServices.IWareHouseService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Requests.RSOAddRequest;
import com.project.ims.Requests.RSOUpdateRequest;
import com.project.ims.Services.DeliveryManService;
import com.project.ims.Services.OrderService;
import com.project.ims.Services.RSOService;

@RestController
@RequestMapping("/api")
public class RSOController {

    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RSOService returnSupplyOrderService;

    @GetMapping("/returnSupplyOrders")
    public List<ReturnSupplyOrder> getReturnSupplyOrders() {
        return returnSupplyOrderService.getAllReturnSupplyOrder();
    }

    @GetMapping("/returnSupplyOrder/{id}")
    public ReturnSupplyOrder getReturnSupplyOrderById(@PathVariable("id") String id) {
        return returnSupplyOrderService.getReturnSupplyOrderById(id);
    }

    @PostMapping("/returnSupplyOrder")
    public ReturnSupplyOrder addReturnSupplyOrder(@RequestBody RSOAddRequest data) {

        ReturnSupplyOrder returnSupplyOrder = new ReturnSupplyOrder();

        Random rand = new Random();
        String id = "rso" + rand.nextInt(10000);
        returnSupplyOrder.setId(id);

        // set date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        returnSupplyOrder.setDate_time(formattedDateTime);

        Order order = orderService.getOrderById(data.getOrder_id());
        returnSupplyOrder.setOrder_id(data.getOrder_id());
        returnSupplyOrder.setWarehouse_id(order.getWarehouse_id());
        returnSupplyOrder.setProduct_id(order.getProduct_id());
        returnSupplyOrder.setQuantity(order.getQuantity());
        returnSupplyOrder.setRefund_amount(order.getTotal_amount());
        returnSupplyOrder.setDelivery_address(data.getDelivery_address());
        returnSupplyOrder.setReturn_reason(data.getReturn_reason());
        returnSupplyOrder.setStatus("pending");
        returnSupplyOrder.setSupplier_id(data.getSupplier_id());
        
        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(order.getWarehouse_id());
        
        for (DeliveryMan d : deliveryMen) {
            if (d.getStatus() == "available") {
                d.setStatus("unavailable");
                returnSupplyOrder.setDelivery_man_id(d.getId());
                deliveryManService.updateDeliveryMan(d);
                break;
            }
        }
        
        returnSupplyOrderService.addReturnSupplyOrder(returnSupplyOrder);
        return returnSupplyOrder;
    }

    @PostMapping("/returnSupplyOrder/{id}/status")
    public ReturnSupplyOrder updateReturnSupplyOrderStatus(@PathVariable("id") String id,
            @RequestBody String status) {
        ReturnSupplyOrder returnSupplyOrder = returnSupplyOrderService.getReturnSupplyOrderById(id);

        if(status.equals("delivered")) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            returnSupplyOrder.setDelivered_date_time(formattedDateTime);

            DeliveryMan m = deliveryManService.getDeliveryManById(returnSupplyOrder.getDelivery_man_id());

            m.setStatus("available");

            deliveryManService.updateDeliveryMan(m);
        }

        returnSupplyOrder.setStatus(status);
        return returnSupplyOrderService.updateReturnSupplyOrder(returnSupplyOrder);
    }
    
    @PostMapping("/returnSupplyOrder/{id}")
    public ReturnSupplyOrder updateReturnSupplyOrder(@PathVariable("id") String id,
            @RequestBody RSOUpdateRequest data) {
        ReturnSupplyOrder returnSupplyOrder = returnSupplyOrderService.getReturnSupplyOrderById(id);
        returnSupplyOrder.setStatus(data.getStatus());
        returnSupplyOrder.setDate_time(data.getDate_time());
        returnSupplyOrder.setDelivered_date_time(data.getDelivered_date_time());
        returnSupplyOrder.setDelivery_man_id(data.getDelivery_man_id());
        returnSupplyOrder.setOrder_id(data.getOrder_id());
        returnSupplyOrder.setProduct_id(data.getProduct_id());
        returnSupplyOrder.setQuantity(data.getQuantity());
        returnSupplyOrder.setRefund_amount(data.getRefund_amount());
        returnSupplyOrder.setWarehouse_id(data.getWarehouse_id());
        returnSupplyOrder.setDelivery_address(data.getDelivery_address());
        returnSupplyOrder.setReturn_reason(data.getReturn_reason());
        returnSupplyOrder.setSupplier_id(data.getSupplier_id());
        return returnSupplyOrderService.updateReturnSupplyOrder(returnSupplyOrder);
    }
    
    @DeleteMapping("/returnSupplyOrder/{id}")
    public void deleteReturnSupplyOrder(@PathVariable("id") String id) {
        returnSupplyOrderService.deleteReturnSupplyOrder(id);
    }
}
