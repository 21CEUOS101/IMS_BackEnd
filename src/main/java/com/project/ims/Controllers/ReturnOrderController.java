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
import org.springframework.web.bind.annotation.RequestParam;
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
import com.project.ims.Models.Product;
import com.project.ims.Models.ReturnOrder;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Models.Supplier;
import com.project.ims.Requests.ReturnOrderAddRequest;
import com.project.ims.Requests.ReturnOrderUpdateRequest;

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
    public ReturnOrder updateReturnOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status) {

        ReturnOrder returnOrder = returnOrderService.getReturnOrderById(id);

        returnOrder.setStatus(status);

        if (status.equals("approved")) {
            Order order = orderService.getOrderById(returnOrder.getOrder_id());
            order.setStatus("returned");
            orderService.updateOrder(order);

            List<DeliveryMan> deliveryMans = deliveryManService.getAllDeliveryManByWarehouse(order.getWarehouse_id());

            for(DeliveryMan i : deliveryMans)
            {
                if (i.getStatus() == "available") {
                    i.setStatus("unavailable");
                    deliveryManService.updateDeliveryMan(i);
                    returnOrder.setDelivery_man_id(i.getId());
                }
            }
            
            if(returnOrder.getDelivery_man_id() == null)
            {
                throw new RuntimeException("Deliveryman not currently available");
            }

        } else if (status.equals("rejected")) {

            Order order = orderService.getOrderById(returnOrder.getOrder_id());
            order.setStatus("delivered");
            orderService.updateOrder(order);

            DeliveryMan m = deliveryManService.getDeliveryManById(returnOrder.getDelivery_man_id());

            m.setStatus("available");

            deliveryManService.updateDeliveryMan(m);

        } else if (status.equals("arrived")) {
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

            returnSupplyOrderService.addReturnSupplyOrder(returnSupplyOrder);

            List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(order.getWarehouse_id());
            
            for (DeliveryMan d : deliveryMen) {
                if (d.getStatus() == "available") {
                    d.setStatus("unavailable");
                    returnSupplyOrder.setDelivery_man_id(d.getId());
                    deliveryManService.updateDeliveryMan(d);
                    break;
                }
            }

        }

        // set date and time

        return returnOrderService.updateReturnOrder(returnOrder);
    }

    // update return order

    @PostMapping("/returnOrder/{id}")
    public ReturnOrder updateReturnOrder(@PathVariable("id") String id, @RequestBody ReturnOrderUpdateRequest data) {

        ReturnOrder returnOrder = returnOrderService.getReturnOrderById(id);
        returnOrder.setPickup_address(data.getPickup_address());
        returnOrder.setReturn_reason(data.getReturn_reason());
        returnOrder.setStatus(data.getStatus());

        Order order = orderService.getOrderById(returnOrder.getOrder_id());
        returnOrder.setProduct_id(order.getProduct_id());
        returnOrder.setQuantity(order.getQuantity());
        returnOrder.setWarehouse_id(order.getWarehouse_id());
        returnOrder.setRefund_amount(order.getTotal_amount());
        returnOrder.setOrder_id(order.getId());
        returnOrder.setCustomer_id(order.getCustomer_id());

        returnOrder.setDate_time(data.getDate_time());
        returnOrder.setDelivered_date_time(data.getDelivered_date_time());
        returnOrder.setDelivery_man_id(data.getDelivery_man_id());

        return returnOrderService.updateReturnOrder(returnOrder);
    }

    // get return orders by warehouse id
    // @GetMapping("/returnOrders/warehouse/{id}")
    // public List<ReturnOrder> getReturnOrdersByWarehouseId(@PathVariable String id) {
    //     return returnOrderService.getAllReturnOrderByWarehouseId(id);
    // }

    // delete return order

    @DeleteMapping("/returnOrder/{id}")
    public void deleteReturnOrder(@PathVariable("id") String id) {
        returnOrderService.deleteReturnOrder(id);
    }
}
