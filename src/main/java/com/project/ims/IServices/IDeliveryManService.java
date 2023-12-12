package com.project.ims.IServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.ims.Models.DeliveryMan;

@Service
public interface IDeliveryManService {

    public List<DeliveryMan> getAllDeliveryMan();

    public List<DeliveryMan> getAllDeliveryManByWarehouse(String wareHouseId);
    
    public DeliveryMan addDeliveryMan(DeliveryMan deliveryMan);

    public DeliveryMan updateDeliveryMan(DeliveryMan deliveryMan);

    public DeliveryMan getDeliveryManById(String id);

    public void deleteDeliveryMan(String id);
}
