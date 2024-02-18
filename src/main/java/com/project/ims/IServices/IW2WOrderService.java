package com.project.ims.IServices;
import java.util.List;
import org.springframework.stereotype.Service;
import com.project.ims.Models.W2WOrder;

@Service
public interface IW2WOrderService {
    
    public List<W2WOrder> getAllW2WOrder();
    public W2WOrder getW2WOrderById(String id);
    public List<W2WOrder> getAllW2WOrderByOrderId(String orderId);
    public W2WOrder addW2WOrder(W2WOrder w2wOrder);
    public W2WOrder updateW2WOrder(W2WOrder w2wOrder);
    public void deleteW2WOrder(String id);
    public W2WOrder updateW2WOrderStatus(String id, String status);
}
