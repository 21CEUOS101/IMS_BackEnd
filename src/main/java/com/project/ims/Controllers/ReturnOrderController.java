package com.project.ims.Controllers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.project.ims.Models.Order;
import com.project.ims.Models.ReturnOrder;
import com.project.ims.Requests.ReturnOrderAddRequest;

@RestController
@RequestMapping("/api")
public class ReturnOrderController {
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

    @Autowired
    private IReturnOrderService returnOrderService;

    @Autowired
    private ISupplyOrderService supplyOrderService;

    @Autowired
    private IW2WOrderService w2wOrderService;

    @Autowired
    private IRSOService returnSupplyOrderService;

    // Return Order APIs


    // get all return orders
    @GetMapping("/returnOrders")
    public List<ReturnOrder> getReturnOrders() {
        return returnOrderService.getAllReturnOrder();
    }

    // get return order by id
    @GetMapping("/returnOrder/{id}")
    public ReturnOrder getReturnOrderById(@PathVariable String id) {
        return returnOrderService.getReturnOrderById(id);
    }

    // get return orders by customer id
    @GetMapping("/returnOrders/customer/{id}")
    public List<ReturnOrder> getReturnOrdersByCustomerId(@PathVariable String id) {
        return returnOrderService.getAllReturnOrderByCustomerId(id);
    }

    // create return order
    @PostMapping("/returnOrder")
    public ReturnOrder createReturnOrder(@RequestBody ReturnOrderAddRequest data) {

        ReturnOrder returnOrder = new ReturnOrder();
        Random rand = new Random();
        String id = "r" + rand.nextInt(1000000);
        returnOrder.setId(id);
        returnOrder.setCustomer_id(data.getCustomer_id());
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
        returnOrder.setWarehouse_id(order.getWarehouse_id());
        returnOrder.setRefund_amount(order.getTotal_amount());
        returnOrder.setOrder_id(order.getId());

        return returnOrderService.addReturnOrder(returnOrder);

    }
    
    // update return order status
    @PostMapping("/returnOrder/{id}/status")
    public ReturnOrder updateReturnOrderStatus(@PathVariable("id") String id, @RequestBody ReturnOrderAddRequest data) {

        ReturnOrder returnOrder = returnOrderService.getReturnOrderById(id);

        returnOrder.setStatus(data.getStatus());

        // set date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        returnOrder.setDelivered_date_time(formattedDateTime);

        return returnOrderService.updateReturnOrder(returnOrder);
    }
}
