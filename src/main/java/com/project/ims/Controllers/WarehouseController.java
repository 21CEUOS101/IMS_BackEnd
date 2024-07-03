package com.project.ims.Controllers;

// imports
import org.springframework.web.bind.annotation.RestController;

import com.project.ims.Models.Order;
import com.project.ims.Models.ReturnOrder;
import com.project.ims.Models.W2WOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Requests.WareHouseAddRequest;
import com.project.ims.Services.WareHouseService;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"https://ashish2901-ims.vercel.app/","http://localhost:3000","https://ims-frontend-eight.vercel.app/"}, allowedHeaders = "*", allowCredentials = "true")
public class WarehouseController {

    // necessary dependency injections
    @Autowired
    private WareHouseService wareHouseService;

    // controllers

    // get warehouse by id
    @GetMapping("/warehouse/{id}")
    public WareHouse getWareHouseById(@PathVariable String id) {
        try{
            WareHouse wareHouse = wareHouseService.getWareHouseById(id);
            return wareHouse;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
 
    @GetMapping("/warehouse")
    public List<WareHouse> getAllWareHouses() {
        try{
            List<WareHouse> wareHouses = wareHouseService.getAllWareHouse();
            return wareHouses;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    @PostMapping("/warehouse")
    public WareHouse createWarehouse(@RequestBody WareHouseAddRequest data) {

        String id = generateId();

        WareHouse wareHouse = new WareHouse();
        wareHouse.setId(id);
        wareHouse.setName(data.getName());
        wareHouse.setAddress(data.getAddress());
        wareHouse.setPincode(data.getPincode());
        wareHouse.setManager_id(data.getManager_id());
        wareHouse.setStatus(data.getStatus());
        wareHouse.setProduct_ids(data.getProduct_ids());
        wareHouse.setQuantities(data.getQuantities());
        wareHouse.setHigherLimits(data.getHigherLimits());
        wareHouse.setLowerLimits(data.getLowerLimits());

        try{
            wareHouseService.addWareHouse(wareHouse);
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }

        return wareHouse;

    }
    
    @PostMapping("/warehouse/{id}")
    public WareHouse updateWareHouse(@PathVariable String id,@RequestBody WareHouseAddRequest data) {

        WareHouse wareHouse = wareHouseService.getWareHouseById(id);
        wareHouse.setName(data.getName());
        wareHouse.setAddress(data.getAddress());
        wareHouse.setPincode(data.getPincode());
        wareHouse.setManager_id(data.getManager_id());
        wareHouse.setStatus(data.getStatus());
        wareHouse.setProduct_ids(data.getProduct_ids());
        wareHouse.setQuantities(data.getQuantities());
        wareHouse.setHigherLimits(data.getHigherLimits());
        wareHouse.setLowerLimits(data.getLowerLimits());

        try{
            wareHouseService.updateWareHouse(wareHouse);
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }

        return wareHouse;
    }
    
    @DeleteMapping("/warehouse/{id}")
    public void deleteWareHouse(@PathVariable String id) {

        try{
            wareHouseService.deleteWareHouse(id);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public String generateId() {
        // id generation
        Random rand = new Random();
        String id = 'w' + String.valueOf(rand.nextInt(1000000));

        return id;
    }

    // get all orders by warehouse
    @GetMapping("/warehouse/{id}/orders")
    public List<Order> getOrdersByWarehouse(@PathVariable String id) {
        try {
            List<Order> orders = wareHouseService.getOrdersByWareHouse(id);
            return orders;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // get all return orders by warehouse
    @GetMapping("/warehouse/{id}/return-orders")
    public List<ReturnOrder> getReturnOrdersByWarehouse(@PathVariable String id) {
        try {
            List<ReturnOrder> orders = wareHouseService.getReturnOrdersByWareHouse(id);
            return orders;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // get all w2w orders by warehouse
    @GetMapping("/warehouse/{id}/w2w-orders")
    public List<W2WOrder> getW2WOrdersByWarehouse(@PathVariable String id) {
        try {
            List<W2WOrder> orders = wareHouseService.getW2WOrdersByWareHouse(id);
            return orders;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

 

    
}
