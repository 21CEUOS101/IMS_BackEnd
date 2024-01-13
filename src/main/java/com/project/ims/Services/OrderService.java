package com.project.ims.Services;

// imports
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IOrderService;
import com.project.ims.Models.Order;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;

@Component
@Service
public class OrderService implements IOrderService {

    // necessary dependency Injections
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    // Services

    @Override
    public Order getOrderById(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

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

        String product_id = order.getProduct_id();
        String quantity = order.getQuantity();

        Integer price = Integer.parseInt(productRepo.findById(product_id).get().getPrice());
        Integer q = Integer.parseInt(quantity);

        totalPrice += price * q;

        if(totalPrice != Integer.parseInt(order.getTotal_amount()))
        {
            throw new RuntimeException("Total price is not valid");
        }

        return orderRepo.save(order);
    }

    @Override
    public Order updateOrder(Order order) {
        
        // Checking if the order data is null or not
        if(order == null)
        {
            throw new RuntimeException("Order data shouldn't be null");
        }

        return orderRepo.save(order);
    }

    @Override
    public void deleteOrder(String id) {

        // Checking if the order ID is valid or not
        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }
        else if(!orderRepo.existsById(id))
        {
            throw new RuntimeException("Order with id " + id + " does not exist");
        }

        orderRepo.deleteById(id);
    }

    public List<Order> getAllOrder() {
        return orderRepo.findAll();
    }

    @Override
    public List<Order> getAllOrderByCustomerId(String id) {
        return orderRepo.findByCustomerId(id);
    }
    
}
