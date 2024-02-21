package com.project.ims.Services;

import java.util.ArrayList;
import java.util.HashMap;
// imports
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.ims.IServices.IWManagerService;

import com.project.ims.Models.WareHouse_Manager;

import com.project.ims.Repo.WManagerRepo;


@Service
public class WManagerService implements IWManagerService {

    // necessary dependency Injections
    @Autowired
    private WManagerRepo wManagerRepo;

 

    
    @Override
    public WareHouse_Manager getWManagerById(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

        return wManagerRepo.findById(id).orElse(null);
    }

    @Override
    public WareHouse_Manager addWManager(WareHouse_Manager wManager) {

        if (wManager.getId() == null || wManager.getId().isEmpty())
        {
            throw new RuntimeException("WareHouse Manager ID cannot be empty");
        }
        else if (wManagerRepo.existsById(wManager.getId()))
        {
            throw new RuntimeException("WareHouse Manager ID already exists");
        }
        else if (wManager.getId().charAt(0) != 'm')
        {
            throw new RuntimeException("WareHouse Manager ID must start with 'm'");
        }

        return wManagerRepo.save(wManager);
    }

    @Override
    public WareHouse_Manager updateWManager(WareHouse_Manager wManager) {
        
        if (wManager == null)
        {
            throw new RuntimeException("WareHouse Manager data shouldn't be null");
        }

        return wManagerRepo.save(wManager);
    }

    @Override
    public void deleteWManager(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }
        else if (!wManagerRepo.existsById(id))
        {
            throw new RuntimeException("WareHouse Manager ID does not exist");
        }
        wManagerRepo.deleteById(id);
    }

    @Override
    public List<WareHouse_Manager> getAllWManager() {
        return wManagerRepo.findAll();
    }
   
     public String generateId() {
        Random rand = new Random();
        int random = rand.nextInt(1000000);
        String id = "so" + random;
        return id;
    }
    
}
/*  private String product_id;

private String quantity;//

private String supplier_id;

private String warehouse_id;

private String payment_method;

private String transaction_id;

private String pickup_address;

private boolean isdelivery_man_Available;
*/
