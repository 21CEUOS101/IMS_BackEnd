package com.project.ims.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// imports
import java.util.List;
import java.util.Map;
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
import com.project.ims.Models.SupplyOrder;
import com.project.ims.Models.WareHouse;
import com.project.ims.Requests.SupplyOrderAddRequest;
import com.project.ims.Requests.SupplyOrderUpdateRequest;
import com.project.ims.Services.SupplyOrderService;
import com.project.ims.Utils.IdGenerator;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173","https://ashish2901-ims.vercel.app/","http://localhost:3000","http://localhost:3001","https://ims-frontend-eight.vercel.app/"}, allowedHeaders = "*", allowCredentials = "true")
public class SupplyOrderController {

    private static final Logger logger = LoggerFactory.getLogger(SupplyOrderController.class);

    // necessary dependency injections

    @Autowired
    private SupplyOrderService supplyOrderService;
    
    // controllers

    // get all supply orders
    @GetMapping("/supply-order")
    public List<SupplyOrder> getAllSupplyOrders() {
        try{
            List<SupplyOrder> supplyOrders = supplyOrderService.getAllSupplyOrder();
            return supplyOrders;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @GetMapping("/supply-order/{id}")
    public SupplyOrder getSupplyOrder(@PathVariable String id) {
        try{
            SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(id);
            return supplyOrder;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @PostMapping("/supply-order")
    public SupplyOrder addSupplyOrder(@RequestBody SupplyOrderAddRequest data) {

        String id = IdGenerator.generate("so");
        SupplyOrder supplyOrder = new SupplyOrder();
        supplyOrder.setId(id);
        supplyOrder.setProductId(data.getProductId());
        supplyOrder.setQuantity(data.getQuantity());
        supplyOrder.setSupplierId(data.getSupplierId());
        supplyOrder.setWarehouseId(data.getWarehouseId());
        supplyOrder.setPaymentMethod(data.getPaymentMethod());
        supplyOrder.setDeliveryManAvailable(false);
        if ("online".equals(data.getPaymentMethod())) {
            supplyOrder.setTransactionId(data.getTransactionId());
        }

        supplyOrder.setPickupAddress(data.getPickupAddress());

        try{
            supplyOrderService.addSupplyOrder(supplyOrder);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }

        return supplyOrder;
    }
    
    @PostMapping("/supply-order/{id}/status")
    public SupplyOrder updateSupplyOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status) {
        
        try{
          SupplyOrder supplyOrder = supplyOrderService.updateSupplyOrderStatus(id, status);
          return supplyOrder;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    // update supply order
    @PostMapping("/supply-order/{id}")
    public SupplyOrder updateSupplyOrder(@PathVariable("id") String id, @RequestBody SupplyOrderUpdateRequest data) {
        SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(id);
        supplyOrder.setDateTime(data.getDateTime());
        supplyOrder.setDeliveredDateTime(data.getDeliveredDateTime());
        supplyOrder.setDeliveryManId(data.getDeliveryManId());
        supplyOrder.setPaymentMethod(data.getPaymentMethod());
        supplyOrder.setPickupAddress(data.getPickupAddress());
        supplyOrder.setProductId(data.getProductId());
        supplyOrder.setQuantity(data.getQuantity());
        supplyOrder.setStatus(data.getStatus());
        supplyOrder.setSupplierId(data.getSupplierId());
        supplyOrder.setTotalAmount(data.getTotalAmount());
        supplyOrder.setTransactionId(data.getTransactionId());
        supplyOrder.setWarehouseId(data.getWarehouseId());
        supplyOrder.setDeliveryManAvailable(data.isDeliveryManAvailable());
    
        
        try{
            supplyOrderService.updateSupplyOrder(supplyOrder);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }

        return supplyOrder;
    }
    @PostMapping("/supply-order/SetSupplyorderByDeliverymanid/{id}/data")
    public SupplyOrder SetIsDelivery_manAvailableByDid(@PathVariable String id, @RequestParam("data") String data) {
        try{
            SupplyOrder supplyOrder = supplyOrderService.SetIsDelivery_manAvailableByDid(id,data);
            return supplyOrder;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @DeleteMapping("/supply-order/{id}")
    public void deleteSupplyOrder(@PathVariable String id) {

        try{
            supplyOrderService.deleteSupplyOrder(id);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
    // All Supplier
    @GetMapping("/supply-order/getSupplyorderPending/{id}")
    public List<Map<String,Object>> getSupplyorderPendingBySuppilerID(@PathVariable String id) {
        try{
            List<Map<String,Object>> supplyOrder = supplyOrderService.getSupplyorderPending(id);
            return supplyOrder;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getallapprovedbutisDFBySid/{id}")
    public List<Map<String ,Object>> getallapprovedbutisDFBySid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallapprovedbutisDF(id);

            return so;
         
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getallapprovedbutisDTBySid/{id}")
    public List<Map<String ,Object>> getallapprovedbutisDTBySid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallapprovedbutisDT(id);

            return so;
         
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getallDeliveredordersBySid/{id}")
    public List<Map<String ,Object>> getallDeliveredordersBySid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallDeliveredorders(id);

            return so;
         
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getallcancelledBySid/{id}")
    public List<Map<String ,Object>> getallcancelledBySid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallcancelledBySid(id);

            return so;
         
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    


    // All Manager
  @GetMapping("/supply-order/getallDeliveredordersByMid/{id}")
    public List<Map<String ,Object>> getallDeliveredordersByMid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallDeliveredordersByMid(id);

            return so;
         
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getallapprovedbutisDFByMid/{id}")
    public List<Map<String ,Object>> getallapprovedbutisDFByMid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallapprovedbutisDFByMid(id);

            return so;
         
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getallapprovedbutisDTByMid/{id}")
    public List<Map<String ,Object>> getallapprovedbutisDTByMid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallapprovedbutisDTByMid(id);

            return so;
         
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getallcancelledByMid/{id}")
    public List<Map<String ,Object>> getallcancelledByMid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallcancelledByMid(id);

            return so;
         
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getallPendingByWId/{id}")
    public List<Map<String ,Object>> getallPendingByWId(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallPendingByWId(id);

            return so;
         
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    //from waremanager
    @GetMapping("/supply-order/wmanager/checkwarehousebyWid/{id}")
    public List<Map<String,Object>> getCheckWarehouse(@PathVariable String id) {

        try {
            List<Map<String,Object>> wareHouse_Manager = supplyOrderService.getCheckWarehouseByWID(id);
          
            return wareHouse_Manager;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @PostMapping("/supply-order/wmanager/makeSupplierOrderByWId/{id}/data")
    public SupplyOrder makeSupplierOrderByWId(@PathVariable String id,@RequestParam("data") String data) {

        try {
            SupplyOrder so = supplyOrderService.makeSupplierOrderByWId(id,data);
          
            return so;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
       @GetMapping("/supply-order/warehouseDetails/{id}")
    public WareHouse warehouseDetails(@PathVariable String id) {
        try{
            WareHouse wareHouse = supplyOrderService.warehouseDetails(id);
            return wareHouse;
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/warehouse/getallproduct/{id}")
    public List<Map<String ,Object>> AllProduct (@PathVariable String id){
        try{
            List<Map<String ,Object>>  Details = supplyOrderService.AllProduct(id);
            return Details;

        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getsupplyorderstatusABDFbyDId/{id}")
    public List<Map<String ,Object>> getsupplyorderstatusABDFbyDId (@PathVariable String id){
        try{
            List<Map<String ,Object>>  Details = supplyOrderService.getsupplyorderstatusABDFbyDId(id);
            logger.debug("Details: {}", Details);
            return Details;

        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getsupplyorderstatusABDTbyDId/{id}")
    public Map<String ,Object> getsupplyorderstatusABDTbyDId (@PathVariable String id){
        try{
            Map<String ,Object>  Details = supplyOrderService.getsupplyorderstatusABDTbyDId(id);
            logger.debug("Details: {}", Details);
            return Details;

        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @GetMapping("/supply-order/getsupplyorderstatusDbyDId/{id}")
    public List<Map<String ,Object>> getsupplyorderstatusDbyDId (@PathVariable String id){
        try{
            List<Map<String ,Object>>  Details = supplyOrderService.getsupplyorderstatusDbyDId(id);
            return Details;

        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @PostMapping("/supply-order/UpdatestatusDTByDid/{id}/data")
    public SupplyOrder updateStatusDTByDid (@PathVariable String id,@RequestParam("data") String data){
        try{
            SupplyOrder  Details = supplyOrderService.updateStatusDTByDid(id,data);
            return Details;

        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    // generate id
    
}
