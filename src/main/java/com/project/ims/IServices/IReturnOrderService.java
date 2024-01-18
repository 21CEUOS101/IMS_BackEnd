package com.project.ims.IServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.ims.Models.ReturnOrder;

@Service
public interface IReturnOrderService {

    public List<ReturnOrder> getAllReturnOrder();

    public List<ReturnOrder> getAllReturnOrderByCustomerId(String id);
    
    public ReturnOrder getReturnOrderById(String id);
    
    public ReturnOrder addReturnOrder(ReturnOrder returnOrder);
    
    public ReturnOrder updateReturnOrder(ReturnOrder returnOrder);

    public ReturnOrder updateReturnOrderStatus(String id, String status);

    public void deleteReturnOrder(String id);
    
}
