package com.project.ims.IServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.ims.Models.ReturnSupplyOrder;

@Service
public interface IRSOService {
    
        public List<ReturnSupplyOrder> getAllReturnSupplyOrder();
        
        public ReturnSupplyOrder getReturnSupplyOrderById(String id);
        
        public ReturnSupplyOrder addReturnSupplyOrder(ReturnSupplyOrder returnSupplyOrder);
        
        public ReturnSupplyOrder updateReturnSupplyOrder(ReturnSupplyOrder returnSupplyOrder);
    
        public void deleteReturnSupplyOrder(String id);
}
