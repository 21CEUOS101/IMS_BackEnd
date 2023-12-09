package com.project.ims.IServices;

import org.springframework.stereotype.Service;

import com.project.ims.Models.DeliveryMan;

@Service
public interface IDeliveryManService {
    
    public DeliveryMan addDeliveryMan(DeliveryMan deliveryMan);

    public DeliveryMan updateDeliveryMan(DeliveryMan deliveryMan);

    public DeliveryMan getDeliveryManById(String id);

    public void deleteDeliveryMan(String id);
}
