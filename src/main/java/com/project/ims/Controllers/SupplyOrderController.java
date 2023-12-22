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
import com.project.ims.Models.SupplyOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Requests.SupplyOrderAddRequest;
import com.project.ims.Requests.SupplyOrderUpdateRequest;

@RestController
@RequestMapping("/api")
public class SupplyOrderController {
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
    
    @GetMapping("/supplyorders")
    public List<SupplyOrder> getAllSupplyOrders() {
        return supplyOrderService.getAllSupplyOrder();
    }

    @GetMapping("/supplyorder/{id}")
    public SupplyOrder getSupplyOrder(@PathVariable String id) {
        return supplyOrderService.getSupplyOrderById(id);
    }

    @PostMapping("/supplyorder")
    public SupplyOrder addSupplyOrder(@RequestBody SupplyOrderAddRequest data) {
        SupplyOrder supplyOrder = new SupplyOrder();

        Random rand = new Random();
        int random = rand.nextInt(1000000);
        String id = "so" + random;
        supplyOrder.setId(id);
        supplyOrder.setProduct_id(data.getProduct_id());
        supplyOrder.setQuantity(data.getQuantity());
        supplyOrder.setSupplier_id(data.getSupplier_id());
        supplyOrder.setWarehouse_id(data.getWarehouse_id());
        supplyOrder.setPayment_method(data.getPayment_method());

        if (data.getPayment_method().equals("Online")) {
            supplyOrder.setTransaction_id(data.getTransaction_id());
        }

        supplyOrder.setPickup_address(data.getPickup_address());

        int price = Integer.parseInt(productService.getProductById(data.getProduct_id()).getPrice());
        int quantity = Integer.parseInt(data.getQuantity());
        int total_amount = price * quantity;
        supplyOrder.setTotal_amount(Integer.toString(total_amount));

        supplyOrder.setStatus("Pending");

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        supplyOrder.setDate_time(formattedDateTime);

        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(supplyOrder.getWarehouse_id());
            
        for (DeliveryMan d : deliveryMen) {
            if (d.getStatus() == "available") {
                d.setStatus("unavailable");
                supplyOrder.setDelivery_man_id(d.getId());
                deliveryManService.updateDeliveryMan(d);
                break;
            }
        }

        return supplyOrderService.addSupplyOrder(supplyOrder);
    }
    
    @PostMapping("/supplyorder/{id}/status")
    public SupplyOrder updateSupplyOrderStatus(@PathVariable("id") String id, @RequestBody String status) {
        SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(id);

        if (status.equals("delivered")) {

            WareHouse wareHouse = wareHouseService.getWareHouseById(supplyOrder.getWarehouse_id());

            for (int i = 0; i < wareHouse.getProduct_ids().size(); i++) {
                if (wareHouse.getProduct_ids().get(i).equals(supplyOrder.getProduct_id())) {
                    int quantity = Integer.parseInt(wareHouse.getQuantities().get(i));
                    quantity = quantity + Integer.parseInt(supplyOrder.getQuantity());
                    wareHouse.getQuantities().set(i, Integer.toString(quantity));
                }
            }

            wareHouseService.updateWareHouse(wareHouse);

            supplyOrder.setDelivered_date_time(LocalDateTime.now().toString());

        } else if (status.equals("cancelled")) {
            // do nothing
        }

        DeliveryMan m = deliveryManService.getDeliveryManById(supplyOrder.getDelivery_man_id());

        m.setStatus("available");

        deliveryManService.updateDeliveryMan(m);

        supplyOrder.setStatus(status);

        return supplyOrderService.updateSupplyOrder(supplyOrder);
    }

    // update supply order
    @PostMapping("/supplyorder/{id}")
    public SupplyOrder updateSupplyOrder(@PathVariable("id") String id, @RequestBody SupplyOrderUpdateRequest data) {
        SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(id);
        supplyOrder.setDate_time(data.getDate_time());
        supplyOrder.setDelivered_date_time(data.getDelivered_date_time());
        supplyOrder.setDelivery_man_id(data.getDelivery_man_id());
        supplyOrder.setPayment_method(data.getPayment_method());
        supplyOrder.setPickup_address(data.getPickup_address());
        supplyOrder.setProduct_id(data.getProduct_id());
        supplyOrder.setQuantity(data.getQuantity());
        supplyOrder.setStatus(data.getStatus());
        supplyOrder.setSupplier_id(data.getSupplier_id());
        supplyOrder.setTotal_amount(data.getTotal_amount());
        supplyOrder.setTransaction_id(data.getTransaction_id());
        supplyOrder.setWarehouse_id(data.getWarehouse_id());
        return supplyOrderService.updateSupplyOrder(supplyOrder);
    }
    
    @DeleteMapping("/supplyorder/{id}")
    public void deleteSupplyOrder(@PathVariable String id) {
        supplyOrderService.deleteSupplyOrder(id);
    }
}
