package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.ims.IServices.IOrderService;
import com.project.ims.Models.Order;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
public class OrderService implements IOrderService {
    
    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private DeliveryManRepo deliveryManRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private SupplierRepo supplierRepo;

    @Autowired
    private WareHouseRepo wareHouseRepo;

    @Autowired
    private WManagerRepo wManagerRepo;

    @Override
    public Order getOrderById(String id) {
        return orderRepo.findById(id).orElse(null);
    }

    @Override
    public Order addOrder(Order order) {
        
        // Checking if the order ID is valid
        if(order.getId() == null || order.getId().isEmpty())
        {
            throw new RuntimeException("Order ID cannot be empty");
        }
        else if(orderRepo.existsById(order.getId()))
        {
            throw new RuntimeException("Order ID already exists");
        }
        else if (order.getId().charAt(0) != 'o')
        {
            throw new RuntimeException("Order ID must start with 'o'");
        }

        // Checking if total price is valid
        Integer totalPrice = 0;

        for(int i = 0; i < order.getProduct_ids().size(); i++)
        {
            String product_id = order.getProduct_ids().get(i);
            String quantity = order.getQuantities().get(i);

            if(!productRepo.existsById(product_id))
            {
                throw new RuntimeException("Product with id " + product_id + " does not exist");
            }

            Integer price = Integer.parseInt(productRepo.findById(product_id).get().getPrice());
            Integer quantityInt = Integer.parseInt(quantity);

            totalPrice += price * quantityInt;
        }

        if(totalPrice != Integer.parseInt(order.getTotal_amount()))
        {
            throw new RuntimeException("Total price is not valid");
        }

        return orderRepo.save(order);
    }

    @Override
    public Order updateOrder(Order order) {
        return orderRepo.save(order);
    }

    @Override
    public void deleteOrder(String id) {

        if(!orderRepo.existsById(id))
        {
            throw new RuntimeException("Order with id " + id + " does not exist");
        }

        orderRepo.deleteById(id);
    }

    public List<Order> getAllOrder() {
        return orderRepo.findAll();
    }
    
}
