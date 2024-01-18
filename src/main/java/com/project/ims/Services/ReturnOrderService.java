package com.project.ims.Services;

// imports
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IReturnOrderService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.Product;
import com.project.ims.Models.ReturnOrder;
import com.project.ims.Models.ReturnSupplyOrder;
import com.project.ims.Models.Supplier;
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

        Supplier supplier = supplierService.getSupplierById(product.getSupplier_id());

        returnSupplyOrder.setDelivery_address(supplier.getAddress());
        returnSupplyOrder.setReturn_reason(returnOrder.getReturn_reason());
        returnSupplyOrder.setStatus("shipped");
        returnSupplyOrder.setSupplier_id(product.getSupplier_id());

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
    
}
