package com.project.ims.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// imports
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.W2WOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Requests.W2WOrderAddRequest;
import com.project.ims.Requests.W2WOrderUpdateRequest;
import com.project.ims.Services.DeliveryManService;
import com.project.ims.Services.W2WOrderService;
import com.project.ims.Utils.IdGenerator;
import java.util.Map;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173","https://ashish2901-ims.vercel.app/","http://localhost:3000","http://localhost:3001","https://ims-frontend-eight.vercel.app/"}, allowedHeaders = "*", allowCredentials = "true")
public class W2WOrderController {

    private static final Logger logger = LoggerFactory.getLogger(W2WOrderController.class);

    // necessary dependency injections
    @Autowired
    private W2WOrderService w2wOrderService;
    @Autowired
    private DeliveryManService deliveryManService;
  

    // controllers
    @GetMapping("/w2worder")
    public List<W2WOrder> getAllW2WOrders() {
        
        try{
            List<W2WOrder> w2wOrders = w2wOrderService.getAllW2WOrder();
            return w2wOrders;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    @GetMapping("/w2worder/{id}")
    public W2WOrder getW2WOrderById(@PathVariable("id") String id) {
        try{
            W2WOrder w2wOrder = w2wOrderService.getW2WOrderById(id);
            return w2wOrder;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @PostMapping("/w2worder")
    public W2WOrder addW2WOrder(@RequestBody W2WOrderAddRequest data) {

        String id = IdGenerator.generate("w2w");
        W2WOrder w2wOrder = new W2WOrder();
        w2wOrder.setId(id);
        w2wOrder.setProductId(data.getProductId());
        w2wOrder.setQuantity(data.getQuantity());
        w2wOrder.setWarehouseId(data.getRWarehouseId());
        w2wOrder.setSWarehouseId(data.getSWarehouseId());
        w2wOrder.setOrderId(data.getOrderId());
        w2wOrder.setStatus("shipped");

        try{
            w2wOrderService.addW2WOrder(w2wOrder);
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }
        return w2wOrder;
    }

    @PostMapping("/w2worder/{id}/status")
    public W2WOrder updateW2WOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status) {
        try{
            W2WOrder w2wOrder  = w2wOrderService.updateW2WOrderStatus(id, status);
            return w2wOrder;
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    
    @PostMapping("/w2worder/{id}")
    public W2WOrder updateW2wOrder( @PathVariable("id") String id ,@RequestBody W2WOrderUpdateRequest data)
    {
        W2WOrder w2wOrder = w2wOrderService.getW2WOrderById(id);
        w2wOrder.setProductId(data.getProductId());
        w2wOrder.setQuantity(data.getQuantity());
        w2wOrder.setWarehouseId(data.getRWarehouseId());
        w2wOrder.setSWarehouseId(data.getSWarehouseId());
        w2wOrder.setTotalAmount(data.getTotalAmount());
        w2wOrder.setStatus(data.getStatus());
        w2wOrder.setDateTime(data.getDateTime());
        w2wOrder.setDeliveredDateTime(data.getDeliveredDateTime());
        w2wOrder.setDeliveryManId(data.getDeliveryManId());
        w2wOrder.setOrderId(data.getOrderId());
        
        try{
            w2wOrderService.updateW2WOrder(w2wOrder);
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }

        return w2wOrder;
    }

    @DeleteMapping("/w2worder/{id}")
    public void deleteW2WOrder(@PathVariable("id") String id) {
        try{
            w2wOrderService.deleteW2WOrder(id);
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    @GetMapping("/w2worder/w2worderstatusCByDId/{id}")
    public List<Map<String, Object>> orderstatusCByDId(@PathVariable("id") String id) {
        try {
            List<Map<String, Object>> w2wordersWithCustomer = w2wOrderService.w2worderstatusCByDeliverymanId(id);
            return w2wordersWithCustomer;
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/w2worder/w2worderstatusPByDId/{id}")
    public List<Map<String, Object>> orderstatusPByDId(@PathVariable("id") String id) {
        try {
            List<Map<String, Object>> w2wordersWithCustomer = w2wOrderService.w2worderstatusPByDeliverymanId(id);
            return w2wordersWithCustomer;
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/w2worder/w2worderstatusSByDId/{id}")
    public Map<String, Object> orderstatusSByDId(@PathVariable("id") String id) {
        try {
            Map<String, Object> w2wordersWithCustomer = w2wOrderService.w2worderstatusSByDeliverymanId(id);
            return w2wordersWithCustomer;
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @PostMapping("/w2worder/assignBydeliveryman/{id}/data")
    public W2WOrder assignDeliverymanById(@PathVariable("id") String id,@RequestParam("data") String data) {
       logger.debug(data);
        try {
           W2WOrder w2wOrder = w2wOrderService.getW2WOrderById(data);
           if(w2wOrder == null){
            logger.debug("doesnot exist delivery man or warehouse");
            return null;
           }
           DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
           

           if(w2wOrder.getStatus().equals("pending")){

            w2wOrder.setDeliveryManId(id);
               w2wOrder.setStatus("shipped");
               
               w2wOrderService.updateW2WOrder(w2wOrder);
               deliveryMan.setStatus("unavailable");
               deliveryManService.updateDeliveryMan(deliveryMan);
               return w2wOrder;
           }
           else{
            logger.debug("delivery man is not available");
            return null;

           }
           
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @GetMapping("/w2worder/numberofwarehouseByDid/{id}")
    public HashSet<WareHouse> NumberofwarehouseByDid(@PathVariable("id") String id) {
        try {
           HashSet<WareHouse> allware = w2wOrderService.numberofwarehouse(id);
           return allware;
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
     @GetMapping("/w2worder/totalw2wordercompletedByDid/{id}")
    public String numberofCompletedorders(@PathVariable("id") String id) {
        try {
           
            return String.valueOf(orderstatusCByDId(id).size());
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            return "error";
        }
    } 

    
}
