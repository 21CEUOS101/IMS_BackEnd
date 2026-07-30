package com.project.ims.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
// imports
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.ISupplyOrderService;
import com.project.ims.Models.DeliveryMan;
import com.project.ims.Models.Order;
import com.project.ims.Models.Product;
import com.project.ims.Models.Supplier;
import com.project.ims.Models.SupplyOrder;
import com.project.ims.Models.User;
import com.project.ims.Models.WareHouse;
import com.project.ims.Models.WareHouse_Manager;
import com.project.ims.Repo.SupplyOrderRepo;
import com.project.ims.Requests.SupplyOrderAddRequest;

@Service
public class SupplyOrderService implements ISupplyOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SupplyOrderService.class);

    // necessary dependency Injections
    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private SupplyOrderRepo supplyOrderRepo;

    @Autowired
    private DeliveryManService deliveryManService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private UserService userService;
    @Autowired
    private WManagerService wManagerService;

    // Services

    @Override
    public List<SupplyOrder> getAllSupplyOrder() {
        return supplyOrderRepo.findAll();
    }

    @Override
    public SupplyOrder getSupplyOrderById(String id) {

        if (id == null) {
            throw new RuntimeException("Id shouldn't be null");
        }

        return supplyOrderRepo.findById(id).orElse(null);
    }

    @Override
    public SupplyOrder addSupplyOrder(SupplyOrder supplyOrder) {

        if (supplyOrder.getId() == null || supplyOrder.getId().isEmpty()) {
            throw new RuntimeException("Supply Order ID cannot be empty");
        } else if (supplyOrderRepo.existsById(supplyOrder.getId())) {
            throw new RuntimeException("Supply Order ID already exists");
        } else if (supplyOrder.getId().charAt(0) != 's') {
            throw new RuntimeException("Supply Order ID must start with 's'");
        }

        Product product = productService.getProductById(supplyOrder.getProductId());

        int price = product.getWholeSalePrice();

        int quantity = Integer.parseInt(supplyOrder.getQuantity());
        int total_amount = price * quantity;
        supplyOrder.setTotalAmount(Integer.toString(total_amount));

        supplyOrder.setStatus("pending");

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        supplyOrder.setDateTime(formattedDateTime);

        return supplyOrderRepo.save(supplyOrder);
    }

    @Override
    public SupplyOrder updateSupplyOrder(SupplyOrder supplyOrder) {

        if (supplyOrder == null) {
            throw new RuntimeException("Supply Order data shouldn't be null");
        }

        return supplyOrderRepo.save(supplyOrder);
    }

    @Override
    public void deleteSupplyOrder(String id) {

        if (id == null) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!supplyOrderRepo.existsById(id)) {
            throw new RuntimeException("Supply Order with id " + id + " does not exist");
        }

        supplyOrderRepo.deleteById(id);
    }

    public String assignDeliveryMan(SupplyOrder supplyOrder) {
        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(supplyOrder.getWarehouseId());
        String deliveryManId = null;
        for (DeliveryMan d : deliveryMen) {
            if (d.getStatus().equals("available")) {
                d.setStatus("unavailable");
                deliveryManId = d.getId();
                try {
                    deliveryManService.updateDeliveryMan(d);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return null;
                }

                break;
            }
        }

        return deliveryManId;
    }

    @Override
    public SupplyOrder updateSupplyOrderStatus(String id, String status) {
        SupplyOrder supplyOrder = getSupplyOrderById(id);

        if (status.equals("delivered") && supplyOrder.isDeliveryManAvailable()) {

            WareHouse wareHouse = wareHouseService.getWareHouseById(supplyOrder.getWarehouseId());

            for (int i = 0; i < wareHouse.getProductIds().size(); i++) {
                if (wareHouse.getProductIds().get(i).equals(supplyOrder.getProductId())) {
                    int quantity = Integer.parseInt(wareHouse.getQuantities().get(i));
                    quantity = quantity + Integer.parseInt(supplyOrder.getQuantity());
                    wareHouse.getQuantities().set(i, Integer.toString(quantity));
                }
            }

            try {
                wareHouseService.updateWareHouse(wareHouse);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }

            supplyOrder.setDeliveredDateTime(LocalDateTime.now().toString());

            DeliveryMan m = deliveryManService.getDeliveryManById(supplyOrder.getDeliveryManId());

            m.setStatus("available");
            supplyOrder.setStatus("delivered");
            try {
                deliveryManService.updateDeliveryMan(m);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }

        } else if (status.equals("cancel")) {
            if(supplyOrder.getStatus().equals("pending")){
                supplyOrder.setStatus("cancel");
            }
            else if (supplyOrder.getStatus().equals("approved") && supplyOrder.isDeliveryManAvailable()) {

                DeliveryMan m = deliveryManService.getDeliveryManById(supplyOrder.getDeliveryManId());

                m.setStatus("available");
                supplyOrder.setStatus("cancel");
                try {
                    deliveryManService.updateDeliveryMan(m);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return null;
                }
            }
            else{
               logger.debug("status is different from approved and pending");
            }
        } else if (status.equals("approved")) {
            
            String deliveryManId = assignDeliveryMan(supplyOrder);

            supplyOrder.setDeliveryManId(deliveryManId);

            if (deliveryManId == null) {

            } else {
                supplyOrder.setDeliveryManAvailable(true);

            }
            supplyOrder.setStatus("approved");
        }

        // checking if status is enum of pending, delivered or cancelled

        if (!(status.equals("pending") || status.equals("delivered") || status.equals("cancel")
                || status.equals("approved"))) {
            logger.debug("Invalid status");
            return null;
        }

        // supplyOrder.setStatus(status);

        return supplyOrderRepo.save(supplyOrder);
    }

    public SupplyOrder SetIsDelivery_manAvailableByDid(String id, String order) {
        SupplyOrder so = getSupplyOrderById(order);
        DeliveryMan d = deliveryManService.getDeliveryManById(id);
        if (d == null) {
            logger.debug("delivery donot exist");
            return null;
        }
        if (d.getStatus().equals("unavailable")) {
            logger.debug("delivery man is not free");
            return null;
        }
        if (so == null) {
            logger.debug("supply order is not there");
            return null;
        }

        if (so.getStatus().equals("approved")) {

            so.setDeliveryManId(id);
            so.setDeliveryManAvailable(true);
            d.setStatus("unavailable");
            deliveryManService.updateDeliveryMan(d);
            updateSupplyOrder(so);
        } else {
            logger.debug("supply orders status is not approved");
            return null;
        }
        return so;

    }

    public List<Map<String, Object>> getSupplyorderPending(String id) {
        // Supplier supp = supplierService.getSupplierById(id);
        List<SupplyOrder> so = getAllSupplyOrder();

        List<Map<String, Object>> entries = new ArrayList<>();
        for (SupplyOrder s : so) {
            if (s.getSupplierId().equals(id) && s.getStatus().equals("pending")) {
                Map<String, Object> fi = new HashMap<>();
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManagerId());
                User user = userService.getUserByUserId(ware.getManagerId());
                Product prod = productService.getProductById(s.getProductId());
                fi.put("warehouse", ware);
                fi.put("supplyOrder", s);
                fi.put("manager", wm);
                fi.put("user", user);
                fi.put("product",prod);
                entries.add(fi);
            }
        }
        return entries;

    }

    public List<Map<String, Object>> getallapprovedbutisDF(String id) {
        List<Map<String, Object>> so = new ArrayList<>();
        List<SupplyOrder> allSo = getAllSupplyOrder();
        
        for( SupplyOrder s : allSo){
            if(s.getSupplierId().equals(id) && s.getStatus().equals("approved") && !s.isDeliveryManAvailable() ){
                Map<String ,Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProductId());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManagerId());
                // WareHouse_Manager wm = .getSupplierById(id);
                User user = userService.getUserByUserId(ware.getManagerId());
                ma.put("supplyOrder", s);
                ma.put("product", p);
                ma.put("warehouse", ware);
                ma.put("Manager", wm);
                ma.put("user", user);

                so.add(ma);
            }
        }
        return so;
    }

    public List<Map<String, Object>> getallapprovedbutisDT(String id) {
        List<Map<String, Object>> so = new ArrayList<>();
        List<SupplyOrder> allSo = getAllSupplyOrder();
        
        for( SupplyOrder s : allSo){
            if(s.getSupplierId().equals(id) && s.getStatus().equals("approved") && s.isDeliveryManAvailable() ){
                Map<String ,Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProductId());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManagerId());
                User user = userService.getUserByUserId(ware.getManagerId());
                DeliveryMan d = deliveryManService.getDeliveryManById(s.getDeliveryManId());
                User d_user = userService.getUserByUserId(d.getId());
                ma.put("product", p);
                ma.put("supplyOrder", s);
                ma.put("warehouse", ware);
                ma.put("manager", wm);
                ma.put("Manager_user", user);
                ma.put("delivery_man", d);
                ma.put("D_user", d_user);
                so.add(ma);
            }
        }
        return so;
    }

    public List<Map<String, Object>> getallDeliveredorders(String id) {
        List<Map<String, Object>> so = new ArrayList<>();
        List<SupplyOrder> allSo = getAllSupplyOrder();
        
        for( SupplyOrder s : allSo){
            if(s.getSupplierId().equals(id) && s.getStatus().equals("delivered") && s.isDeliveryManAvailable() ){
                Map<String ,Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProductId());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManagerId());
                User user = userService.getUserByUserId(ware.getManagerId());
                DeliveryMan d = deliveryManService.getDeliveryManById(s.getDeliveryManId());
                User d_user = userService.getUserByUserId(d.getId());
                ma.put("supplyOrder", s);
                ma.put("product", p);
                ma.put("warehouse", ware);
                ma.put("manager", wm);
                ma.put("Manager_user", user);
                ma.put("delivery_man", d);
                ma.put("D_user", d_user);
                so.add(ma);
            }
        }
        return so;
    }

    public List<Map<String, Object>> getallcancelledBySid(String id) {
        List<Map<String, Object>> so = new ArrayList<>();
        List<SupplyOrder> allSo = getAllSupplyOrder();
        
        for( SupplyOrder s : allSo){
            if(s.getSupplierId().equals(id) && s.getStatus().equals("cancel") ){
                Map<String ,Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProductId());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManagerId());
                User user = userService.getUserByUserId(ware.getManagerId());
                ma.put("supplyorder", s);
                ma.put("product", p);
                ma.put("warehouse", ware);
                ma.put("manager", wm);
                ma.put("Manager_user", user);

                so.add(ma);
            }
        }
        return so;
    }

    // manager
    public List<Map<String, Object>> getallDeliveredordersByMid(String id) {
        List<Map<String, Object>> so = new ArrayList<>();
        List<SupplyOrder> allSo = getAllSupplyOrder();
        for (SupplyOrder s : allSo) {
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouseId());
            if (wareHouse == null) {
                logger.debug("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManagerId().equals(id) && s.getStatus().equals("delivered")) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProductId());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                Supplier sup= supplierService.getSupplierById(s.getSupplierId());
                User user = userService.getUserByUserId(sup.getId());
                DeliveryMan d = deliveryManService.getDeliveryManById(s.getDeliveryManId());
                User d_user = userService.getUserByUserId(d.getId());
                ma.put("supplyorder", s);
                ma.put("product", p);
                ma.put("warehouse", ware);
                ma.put("suppiler", sup);
                ma.put("user", user);
                ma.put("delivery_man", d);
                ma.put("d_user", d_user);

                so.add(ma);
            }
        }
        return so;
    }

    public List<Map<String, Object>> getallapprovedbutisDTByMid(String id) {
        List<Map<String, Object>> so = new ArrayList<>();
        List<SupplyOrder> allSo = getAllSupplyOrder();
        for (SupplyOrder s : allSo) {
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouseId());
            if (wareHouse == null) {
                logger.debug("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManagerId().equals(id) && s.getStatus().equals("approved")
                    && s.isDeliveryManAvailable()) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProductId());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                Supplier sup= supplierService.getSupplierById(s.getSupplierId());
                User user = userService.getUserByUserId(sup.getId());
                DeliveryMan d = deliveryManService.getDeliveryManById(s.getDeliveryManId());
                User d_user = userService.getUserByUserId(d.getId());
                ma.put("supplyorder", s);
                ma.put("product", p);
                ma.put("warehouse", ware);
                ma.put("suppiler", sup);
                ma.put("user", user);
                ma.put("delivery_man", d);
                ma.put("d_user", d_user);

                so.add(ma);
            }
        }
        return so;
    }

    public List<Map<String, Object>> getallapprovedbutisDFByMid(String id) {
        List<Map<String, Object>> so = new ArrayList<>();
        List<SupplyOrder> allSo = getAllSupplyOrder();
        for (SupplyOrder s : allSo) {
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouseId());
            if (wareHouse == null) {
                logger.debug("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManagerId().equals(id) && s.getStatus().equals("approved")
                    && !s.isDeliveryManAvailable()) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProductId());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                Supplier sup= supplierService.getSupplierById(s.getSupplierId());
                User user = userService.getUserByUserId(sup.getId());

                ma.put("supplyorder", s);
                ma.put("product", p);
                ma.put("warehouse", ware);
                ma.put("suppiler", sup);
                ma.put("user", user);

                so.add(ma);
            }
        }
        return so;
    }

    public List<Map<String, Object>> getallcancelledByMid(String id) {
        List<Map<String, Object>> so = new ArrayList<>();
        List<SupplyOrder> allSo = getAllSupplyOrder();
        for (SupplyOrder s : allSo) {
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouseId());
            if (wareHouse == null) {
                logger.debug("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManagerId().equals(id) && s.getStatus().equals("cancel")) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProductId());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                Supplier sup= supplierService.getSupplierById(s.getSupplierId());
                User user = userService.getUserByUserId(sup.getId());

                ma.put("supplyorder", s);
                ma.put("product", p);
                ma.put("warehouse", ware);
                ma.put("suppiler", sup);
                ma.put("user", user);

                so.add(ma);
            }
        }
        return so;
    }
    public List<Map<String, Object>> getallPendingByWId(String id) {
        List<Map<String, Object>> so = new ArrayList<>();
        List<SupplyOrder> allSo = getAllSupplyOrder();
        for (SupplyOrder s : allSo) {
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouseId());
            if (wareHouse == null) {
                logger.debug("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManagerId().equals(id) && s.getStatus().equals("pending")) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProductId());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouseId());
                Supplier sup = supplierService.getSupplierById(s.getSupplierId());
                User user = userService.getUserByUserId(sup.getId());

                ma.put("supplyorder", s);
                ma.put("product", p);
                ma.put("warehouse", ware);
                ma.put("suppiler", sup);
                ma.put("user", user);

                so.add(ma);
            }
        }
        return so;
    }

    // from Wmanager
    public List<Map<String, Object>> getCheckWarehouseByWID(String id) {
        WareHouse_Manager wm = wManagerService.getWManagerById(id);
        WareHouse wareHouse = wareHouseService.getWareHouseById(wm.getWarehouseId());
        List<String> p = new ArrayList<>();
        for (int i = 0; i < wareHouse.getProductIds().size(); i++) {
            if (wareHouse.getLowerLimits().get(i) > Integer.parseInt(wareHouse.getQuantities().get(i)) && !isNotSupplyOrderisCreated(wareHouse, wareHouse.getProductIds().get(i),wareHouse.getHigherLimits().get(i) - Integer.parseInt(wareHouse.getQuantities().get(i)))) {
                p.add(wareHouse.getProductIds().get(i));
            }
        }
        List<Map<String, Object>> prodwithsupplier = new ArrayList<>();

        for (int i = 0; i < p.size(); i++) {
            Map<String, Object> p1 = new HashMap<>();
            Product pro = productService.getProductById(p.get(i));
            Supplier sup = supplierService.getSupplierById(pro.getSupplierId());
            User user = userService.getUserByUserId(pro.getSupplierId());
            p1.put("product", pro);
            p1.put("supplier", sup);
            p1.put("user", user);
            prodwithsupplier.add(p1);
        }

        return prodwithsupplier;

    }

    public SupplyOrder makeSupplierOrderByWId(String id, String pid) {
        SupplyOrderAddRequest data = new SupplyOrderAddRequest();
        WareHouse_Manager wm = wManagerService.getWManagerById(id);

        Product prod = productService.getProductById(pid);
        Supplier sup = supplierService.getSupplierById(prod.getSupplierId());
        WareHouse war = wareHouseService.getWareHouseById(wm.getWarehouseId());
        Integer index = war.getProductIds().indexOf(pid);
        Integer quan = war.getHigherLimits().get(index) - Integer.parseInt(war.getQuantities().get(index));
        data.setProductId(pid);
        data.setSupplierId(prod.getSupplierId());
        data.setWarehouseId(wm.getWarehouseId());
        data.setPaymentMethod("cash");
        data.setTransactionId(null);
        data.setPickupAddress(sup.getAddress());
        data.setDeliveryManAvailable(false);
        data.setQuantity(String.valueOf(quan));

        String rsid = generateId();
        SupplyOrder supplyOrder = new SupplyOrder();
        supplyOrder.setId(rsid);
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

        try {
            addSupplyOrder(supplyOrder);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        return supplyOrder;

    }
    public boolean isNotSupplyOrderisCreated( WareHouse w ,String Productid,Integer quan){
        List<SupplyOrder> so = getAllSupplyOrder();
        for(SupplyOrder s : so){
            if(s.getWarehouseId().equals(w.getId()) && s.getProductId().equals(Productid)){
                return true;
            }
        }
        
        return false;
    }

    public WareHouse warehouseDetails(String id) {
        
        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }
        WareHouse_Manager wareManager = wManagerService.getWManagerById(id); 
        WareHouse  ware  = wareHouseService.getWareHouseById(wareManager.getWarehouseId());
        return ware;
        // return wareHouseRepo.findById(id).orElse(null);
    }

    public List<Map<String ,Object>> AllProduct(String id){
        List<Map<String ,Object>> details = new ArrayList<>();

        WareHouse_Manager wm = wManagerService.getWManagerById(id);
        WareHouse w = wareHouseService.getWareHouseById(wm.getWarehouseId());
        for(int i =0;i<w.getProductIds().size();i++){
            Map<String, Object> prod_details = new HashMap<>();
            Product product  =productService.getProductById(w.getProductIds().get(i));
            User user =userService.getUserByUserId(product.getSupplierId());
            Supplier supplier = supplierService.getSupplierById(product.getSupplierId());
            prod_details.put("highlimits" , w.getHigherLimits().get(i));
            prod_details.put("lowlimits" , w.getLowerLimits().get(i));
            prod_details.put("product" , product);
            prod_details.put("user", user);
            prod_details.put("wareQ",w.getQuantities().get(i));
            prod_details.put("supplier", supplier);
            details.add(prod_details);

        }
        return details;

    }
    public List<Map<String,Object>> getsupplyorderstatusABDFbyDId(String id){
        List<Map<String ,Object>> orders = new ArrayList<>();
        List<SupplyOrder> so = supplyOrderRepo.findAll();
        DeliveryMan d = deliveryManService.getDeliveryManById(id);
        if(d == null ){
            logger.debug("delivery is not exist");
            return null;
        }
        for(SupplyOrder s : so ){
            if(s.getWarehouseId().equals(d.getWarehouseId()) && s.getStatus().equals("approved") && !s.isDeliveryManAvailable()){
                Map<String ,Object> AllDetails = new HashMap<>();
                Product po = productService.getProductById(s.getProductId());
                Supplier sup = supplierService.getSupplierById(s.getSupplierId());
                User user = userService.getUserByUserId(sup.getId());
                WareHouse ware  =  wareHouseService.getWareHouseById(s.getWarehouseId());
                User wm = userService.getUserByUserId(ware.getManagerId());
                AllDetails.put("warehouse",ware);
                AllDetails.put("Manager",wm);

                AllDetails.put("user",user);
                AllDetails.put("product" ,po);
                AllDetails.put("suppiler",sup);
                AllDetails.put("supplyorder", s);
                orders.add(AllDetails);
            }
        }
        return orders;
    }
    public Map<String,Object> getsupplyorderstatusABDTbyDId(String id){
        Map<String ,Object> AllDetails = new HashMap<>();
        List<SupplyOrder> so = supplyOrderRepo.findAll();
        DeliveryMan d = deliveryManService.getDeliveryManById(id);
        if(d == null ){
            logger.debug("delivery is not exist");
            return null;
        }
        for(SupplyOrder s : so ){
            if(s.getDeliveryManId() == null ){
                logger.debug("delivery man id is not null");
                break;
            }
            if(s.getWarehouseId().equals(d.getWarehouseId()) && s.getStatus().equals("approved") && s.isDeliveryManAvailable() && s.getDeliveryManId().equals(id)){
               
                Product po = productService.getProductById(s.getProductId());
                Supplier sup = supplierService.getSupplierById(s.getSupplierId());
                AllDetails.put("product" ,po);
                User user = userService.getUserByUserId(sup.getId());
                WareHouse ware  =  wareHouseService.getWareHouseById(s.getWarehouseId());
                User wm = userService.getUserByUserId(ware.getManagerId());
                AllDetails.put("warehouse",ware);
                AllDetails.put("Manager",wm);
                AllDetails.put("user",user);
                AllDetails.put("suppiler",sup);
                AllDetails.put("supplyorder", s);
               
                break;
            }
        }
        return AllDetails;
    }
    public List<Map<String,Object>> getsupplyorderstatusDbyDId(String id){
        List<Map<String ,Object>> orders = new ArrayList<>();
        List<SupplyOrder> so = supplyOrderRepo.findAll();
        DeliveryMan d = deliveryManService.getDeliveryManById(id);
        if(d == null ){
            logger.debug("delivery is not exist");
            return null;
        }
        for(SupplyOrder s : so ){
            if(s.getDeliveryManId() == null ){
                logger.debug("delivery man id is not null");
                break;
            }
            if(s.getStatus().equals("delivered")    && s.getDeliveryManId().equals(id)){
                Map<String ,Object> AllDetails = new HashMap<>();
                Product po = productService.getProductById(s.getProductId());
                Supplier sup = supplierService.getSupplierById(s.getSupplierId());
                User user = userService.getUserByUserId(sup.getId());
                WareHouse ware  =  wareHouseService.getWareHouseById(s.getWarehouseId());
                User wm = userService.getUserByUserId(ware.getManagerId());
                AllDetails.put("warehouse",ware);
                AllDetails.put("Manager",wm);
                AllDetails.put("user",user);
                AllDetails.put("product" ,po);
                AllDetails.put("suppiler",sup);
                AllDetails.put("supplyorder", s);
                orders.add(AllDetails);
                // break;
            }
        }
        return orders;
    }
    public SupplyOrder updateStatusDTByDid(String id , String data ){
         try {
            logger.debug("hello" +data+" " + id);
           SupplyOrder order = getSupplyOrderById(data);
           if(order == null){
            logger.debug("Order Doesnot exists");
            return null;
           }
           DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
           if(deliveryMan == null){
            logger.debug("Delivery man doesnot exists ");
            return null;
           }
          

           if(order.getStatus().equals("approved") &&  deliveryMan.getStatus().equals("available") && !order.isDeliveryManAvailable()){

                order.setDeliveryManId(id);
                order.setStatus("approved");
               order.setDeliveryManAvailable(true);
               updateSupplyOrder(order);
               deliveryMan.setStatus("unavailable");
               deliveryManService.updateDeliveryMan(deliveryMan);
               return order;
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


    public String generateId() {
        Random rand = new Random();
        int random = rand.nextInt(1000000);
        String id = "so" + random;
        return id;
    }
}
