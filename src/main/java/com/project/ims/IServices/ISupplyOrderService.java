package com.project.ims.IServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.ims.Models.SupplyOrder;

@Service
public interface ISupplyOrderService{
    
    public List<SupplyOrder> getAllSupplyOrder();
    
    public SupplyOrder getSupplyOrderById(String id);
    
    public SupplyOrder addSupplyOrder(SupplyOrder supplyOrder);
    
    public SupplyOrder updateSupplyOrder(SupplyOrder supplyOrder);

    public SupplyOrder updateSupplyOrderStatus(String id, String status);

    public void deleteSupplyOrder(String id);
}
