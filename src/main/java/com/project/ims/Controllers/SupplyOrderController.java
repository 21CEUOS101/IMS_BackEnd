package com.project.ims.Controllers;

// imports
import java.util.List;
import java.util.Map;
import java.util.Random;
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

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"https://ashish2901-ims.vercel.app/","http://localhost:3000"}, allowedHeaders = "*", allowCredentials = "true")
public class SupplyOrderController {

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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping("/supply-order")
    public SupplyOrder addSupplyOrder(@RequestBody SupplyOrderAddRequest data) {

        String id = generateId();
        SupplyOrder supplyOrder = new SupplyOrder();
        supplyOrder.setId(id);
        supplyOrder.setProduct_id(data.getProduct_id());
        supplyOrder.setQuantity(data.getQuantity());
        supplyOrder.setSupplierId(data.getSupplier_id());
        supplyOrder.setWarehouse_id(data.getWarehouse_id());
        supplyOrder.setPayment_method(data.getPayment_method());
        supplyOrder.setIsdelivery_man_Available(false);
        if (data.getPayment_method().equals("online")) {
            supplyOrder.setTransaction_id(data.getTransaction_id());
        }

        supplyOrder.setPickup_address(data.getPickup_address());

        try{
            supplyOrderService.addSupplyOrder(supplyOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return null;
        }

    }

    // update supply order
    @PostMapping("/supply-order/{id}")
    public SupplyOrder updateSupplyOrder(@PathVariable("id") String id, @RequestBody SupplyOrderUpdateRequest data) {
        SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(id);
        supplyOrder.setDate_time(data.getDate_time());
        supplyOrder.setDelivered_date_time(data.getDelivered_date_time());
        supplyOrder.setDelivery_man_id(data.getDelivery_man_id());
        supplyOrder.setPayment_method(data.getPayment_method());
        supplyOrder.setPickup_address(data.getPickup_address());
        supplyOrder.setProduct_id(data.getProduct_id());
        supplyOrder.setQuantity(data.getQuantity());
        supplyOrder.setStatus(data.getStatus());
        supplyOrder.setSupplierId(data.getSupplier_id());
        supplyOrder.setTotal_amount(data.getTotal_amount());
        supplyOrder.setTransaction_id(data.getTransaction_id());
        supplyOrder.setWarehouse_id(data.getWarehouse_id());
        supplyOrder.setIsdelivery_man_Available(data.isIsdelivery_man_Available());
    
        
        try{
            supplyOrderService.updateSupplyOrder(supplyOrder);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return supplyOrder;
    }
    @PostMapping("/SetSupplyorderByDeliverymanid/{id}/data")
    public SupplyOrder SetIsDelivery_manAvailableByDid(@PathVariable String id, @RequestParam("data") String data) {
        try{
            SupplyOrder supplyOrder = supplyOrderService.SetIsDelivery_manAvailableByDid(id,data);
            return supplyOrder;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/getallapprovedbutisDFBySid/{id}")
    public List<Map<String ,Object>> getallapprovedbutisDFBySid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallapprovedbutisDF(id);

            return so;
         
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/getallapprovedbutisDTBySid/{id}")
    public List<Map<String ,Object>> getallapprovedbutisDTBySid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallapprovedbutisDT(id);

            return so;
         
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/getallDeliveredordersBySid/{id}")
    public List<Map<String ,Object>> getallDeliveredordersBySid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallDeliveredorders(id);

            return so;
         
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/getallcancelledBySid/{id}")
    public List<Map<String ,Object>> getallcancelledBySid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallcancelledBySid(id);

            return so;
         
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    


    // All Manager
  @GetMapping("/getallDeliveredordersByMid/{id}")
    public List<Map<String ,Object>> getallDeliveredordersByMid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallDeliveredordersByMid(id);

            return so;
         
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/getallapprovedbutisDFByMid/{id}")
    public List<Map<String ,Object>> getallapprovedbutisDFByMid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallapprovedbutisDFByMid(id);

            return so;
         
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/getallapprovedbutisDTByMid/{id}")
    public List<Map<String ,Object>> getallapprovedbutisDTByMid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallapprovedbutisDTByMid(id);

            return so;
         
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/getallcancelledByMid/{id}")
    public List<Map<String ,Object>> getallcancelledByMid(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallcancelledByMid(id);

            return so;
         
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    @GetMapping("/getallPendingByWId/{id}")
    public List<Map<String ,Object>> getallPendingByWId(@PathVariable("id") String id) {
        try {

            List<Map<String ,Object>> so =  supplyOrderService.getallPendingByWId(id);

            return so;
         
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //from waremanager
    @GetMapping("/wmanager/checkwarehousebyWid/{id}")
    public List<Map<String,Object>> getCheckWarehouse(@PathVariable String id) {

        try {
            List<Map<String,Object>> wareHouse_Manager = supplyOrderService.getCheckWarehouseByWID(id);
          
            return wareHouse_Manager;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    @PostMapping("/wmanager/makeSupplierOrderByWId/{id}/data")
    public SupplyOrder makeSupplierOrderByWId(@PathVariable String id,@RequestParam("data") String data) {

        try {
            SupplyOrder so = supplyOrderService.makeSupplierOrderByWId(id,data);
          
            return so;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
       @GetMapping("/warehouseDetails/{id}")
    public WareHouse warehouseDetails(@PathVariable String id) {
        try{
            WareHouse wareHouse = supplyOrderService.warehouseDetails(id);
            return wareHouse;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
    @GetMapping("/warehouse/getallproduct/{id}")
    public List<Map<String ,Object>> AllProduct (@PathVariable String id){
        try{
            List<Map<String ,Object>>  Details = supplyOrderService.AllProduct(id);
            return Details;

        }catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    @GetMapping("/supply-order/getsupplyorderstatusABDFbyDId/{id}")
    public List<Map<String ,Object>> getsupplyorderstatusABDFbyDId (@PathVariable String id){
        try{
            List<Map<String ,Object>>  Details = supplyOrderService.getsupplyorderstatusABDFbyDId(id);
            System.out.println(Details);
            return Details;

        }catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    @GetMapping("/supply-order/getsupplyorderstatusABDTbyDId/{id}")
    public Map<String ,Object> getsupplyorderstatusABDTbyDId (@PathVariable String id){
        try{
            Map<String ,Object>  Details = supplyOrderService.getsupplyorderstatusABDTbyDId(id);
            System.out.println(Details);
            return Details;

        }catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    @GetMapping("/supply-order/getsupplyorderstatusDbyDId/{id}")
    public List<Map<String ,Object>> getsupplyorderstatusDbyDId (@PathVariable String id){
        try{
            List<Map<String ,Object>>  Details = supplyOrderService.getsupplyorderstatusDbyDId(id);
            return Details;

        }catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    @PostMapping("/supply-order/UpdatestatusDTByDid/{id}/data")
    public SupplyOrder UpdatestatusDTByDid (@PathVariable String id,@RequestParam("data") String data){
        try{
            SupplyOrder  Details = supplyOrderService.UpdatestatusDTByDid(id,data);
            return Details;

        }catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    // generate id
    public String generateId() {
        Random rand = new Random();
        int random = rand.nextInt(1000000);
        String id = "so" + random;
        return id;
    }
    
}
