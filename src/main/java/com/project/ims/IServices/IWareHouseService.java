package com.project.ims.IServices;
import java.util.List;
import org.springframework.stereotype.Service;

import com.project.ims.Models.Order;
import com.project.ims.Models.ReturnOrder;
import com.project.ims.Models.W2WOrder;
import com.project.ims.Models.WareHouse;

@Service
public interface IWareHouseService {

    public List<WareHouse> getAllWareHouse();
    public WareHouse addWareHouse(WareHouse wareHouse);
    public WareHouse updateWareHouse(WareHouse wareHouse);
    public WareHouse getWareHouseById(String id);
    public void deleteWareHouse(String id);
    public List<ReturnOrder> getReturnOrdersByWareHouse(String id);
    public List<Order> getOrdersByWareHouse(String id);
    public List<W2WOrder> getW2WOrdersByWareHouse(String id);
   
}
