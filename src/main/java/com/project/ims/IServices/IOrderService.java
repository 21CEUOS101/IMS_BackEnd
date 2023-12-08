package com.project.ims.IServices;

import org.springframework.stereotype.Service;

import com.project.ims.Models.Order;

@Service
public interface IOrderService {
    
    public Order getOrderById(int id);
    public Order addOrder(Order order);
    public Order updateOrder(Order order);
    public void deleteOrder(int id);
}
