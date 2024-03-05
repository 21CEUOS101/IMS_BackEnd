package com.project.ims.Services;

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

        Product product = productService.getProductById(supplyOrder.getProduct_id());

        int price = product.getWhole_sale_price();

        int quantity = Integer.parseInt(supplyOrder.getQuantity());
        int total_amount = price * quantity;
        supplyOrder.setTotal_amount(Integer.toString(total_amount));

        supplyOrder.setStatus("pending");

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        supplyOrder.setDate_time(formattedDateTime);

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
        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryManByWarehouse(supplyOrder.getWarehouse_id());
        String deliveryManId = null;
        for (DeliveryMan d : deliveryMen) {
            if (d.getStatus().equals("available")) {
                d.setStatus("unavailable");
                deliveryManId = d.getId();
                try {
                    deliveryManService.updateDeliveryMan(d);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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

        if (status.equals("delivered") && supplyOrder.isIsdelivery_man_Available()) {

            WareHouse wareHouse = wareHouseService.getWareHouseById(supplyOrder.getWarehouse_id());

            for (int i = 0; i < wareHouse.getProduct_ids().size(); i++) {
                if (wareHouse.getProduct_ids().get(i).equals(supplyOrder.getProduct_id())) {
                    int quantity = Integer.parseInt(wareHouse.getQuantities().get(i));
                    quantity = quantity + Integer.parseInt(supplyOrder.getQuantity());
                    wareHouse.getQuantities().set(i, Integer.toString(quantity));
                }
            }

            try {
                wareHouseService.updateWareHouse(wareHouse);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }

            supplyOrder.setDelivered_date_time(LocalDateTime.now().toString());

            DeliveryMan m = deliveryManService.getDeliveryManById(supplyOrder.getDelivery_man_id());

            m.setStatus("available");
            supplyOrder.setStatus("delivered");
            try {
                deliveryManService.updateDeliveryMan(m);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }

        } else if (status.equals("cancel")) {
            if(supplyOrder.getStatus().equals("pending")){
                supplyOrder.setStatus("cancel");
            }
            else if (supplyOrder.getStatus().equals("approved") && supplyOrder.isIsdelivery_man_Available()) {

                DeliveryMan m = deliveryManService.getDeliveryManById(supplyOrder.getDelivery_man_id());

                m.setStatus("available");
                supplyOrder.setStatus("cancel");
                try {
                    deliveryManService.updateDeliveryMan(m);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
            else{
               System.out.println("status is different from approved and pending");
            }
        } else if (status.equals("approved")) {
            
            String deliveryManId = assignDeliveryMan(supplyOrder);

            supplyOrder.setDelivery_man_id(deliveryManId);

            if (deliveryManId == null) {

            } else {
                supplyOrder.setIsdelivery_man_Available(true);

            }
            supplyOrder.setStatus("approved");
        }

        // checking if status is enum of pending, delivered or cancelled

        if (!(status.equals("pending") || status.equals("delivered") || status.equals("cancel")
                || status.equals("approved"))) {
            System.out.println("Invalid status");
            return null;
        }

        // supplyOrder.setStatus(status);

        return supplyOrderRepo.save(supplyOrder);
    }

    public SupplyOrder SetIsDelivery_manAvailableByDid(String id, String order) {
        SupplyOrder so = getSupplyOrderById(order);
        DeliveryMan d = deliveryManService.getDeliveryManById(id);
        if (d == null) {
            System.out.println("delivery donot exist");
            return null;
        }
        if (d.getStatus().equals("unavailable")) {
            System.out.println("delivery man is not free");
            return null;
        }
        if (so == null) {
            System.out.println("supply order is not there");
            return null;
        }

        if (so.getStatus().equals("approved")) {

            so.setDelivery_man_id(id);
            so.setIsdelivery_man_Available(true);
            d.setStatus("unavailable");
            deliveryManService.updateDeliveryMan(d);
            updateSupplyOrder(so);
        } else {
            System.out.println("supply orders status is not approved");
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
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManager_id());
                User user = userService.getUserByUserId(ware.getManager_id());
                Product prod = productService.getProductById(s.getProduct_id());
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
<<<<<<< Updated upstream
        
        for( SupplyOrder s : allSo){
            if(s.getSupplierId().equals(id) && s.getStatus().equals("approved") && !s.isIsdelivery_man_Available() ){
                Map<String ,Object> ma = new HashMap<>();
=======

        for (SupplyOrder s : allSo) {
            if (s.getSupplier_id().equals(id) && s.getStatus().equals("approved") && !s.isIsdelivery_man_Available()) {
                Map<String, Object> ma = new HashMap<>();
>>>>>>> Stashed changes
                Product p = productService.getProductById(s.getProduct_id());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManager_id());
                // WareHouse_Manager wm = .getSupplierById(id);
                User user = userService.getUserByUserId(ware.getManager_id());
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
<<<<<<< Updated upstream
        
        for( SupplyOrder s : allSo){
            if(s.getSupplierId().equals(id) && s.getStatus().equals("approved") && s.isIsdelivery_man_Available() ){
                Map<String ,Object> ma = new HashMap<>();
=======

        for (SupplyOrder s : allSo) {
            if (s.getSupplier_id().equals(id) && s.getStatus().equals("approved") && s.isIsdelivery_man_Available()) {
                Map<String, Object> ma = new HashMap<>();
>>>>>>> Stashed changes
                Product p = productService.getProductById(s.getProduct_id());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManager_id());
                User user = userService.getUserByUserId(ware.getManager_id());
                DeliveryMan d = deliveryManService.getDeliveryManById(s.getDelivery_man_id());
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
<<<<<<< Updated upstream
        
        for( SupplyOrder s : allSo){
            if(s.getSupplierId().equals(id) && s.getStatus().equals("delivered") && s.isIsdelivery_man_Available() ){
                Map<String ,Object> ma = new HashMap<>();
=======

        for (SupplyOrder s : allSo) {
            if (s.getSupplier_id().equals(id) && s.getStatus().equals("delivered") && s.isIsdelivery_man_Available()) {
                Map<String, Object> ma = new HashMap<>();
>>>>>>> Stashed changes
                Product p = productService.getProductById(s.getProduct_id());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManager_id());
                User user = userService.getUserByUserId(ware.getManager_id());
                DeliveryMan d = deliveryManService.getDeliveryManById(s.getDelivery_man_id());
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
<<<<<<< Updated upstream
        
        for( SupplyOrder s : allSo){
            if(s.getSupplierId().equals(id) && s.getStatus().equals("cancel") ){
                Map<String ,Object> ma = new HashMap<>();
=======

        for (SupplyOrder s : allSo) {
            if (s.getSupplier_id().equals(id) && s.getStatus().equals("cancel")) {
                Map<String, Object> ma = new HashMap<>();
>>>>>>> Stashed changes
                Product p = productService.getProductById(s.getProduct_id());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
                WareHouse_Manager wm = wManagerService.getWManagerById(ware.getManager_id());
                User user = userService.getUserByUserId(ware.getManager_id());
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
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouse_id());
            if (wareHouse == null) {
                System.out.println("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManager_id().equals(id) && s.getStatus().equals("delivered")) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProduct_id());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
<<<<<<< Updated upstream
                Supplier sup= supplierService.getSupplierById(s.getSupplierId());
=======
                Supplier sup = supplierService.getSupplierById(s.getSupplier_id());
>>>>>>> Stashed changes
                User user = userService.getUserByUserId(sup.getId());
                DeliveryMan d = deliveryManService.getDeliveryManById(s.getDelivery_man_id());
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
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouse_id());
            if (wareHouse == null) {
                System.out.println("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManager_id().equals(id) && s.getStatus().equals("approved")
                    && s.isIsdelivery_man_Available()) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProduct_id());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
<<<<<<< Updated upstream
                Supplier sup= supplierService.getSupplierById(s.getSupplierId());
=======
                Supplier sup = supplierService.getSupplierById(s.getSupplier_id());
>>>>>>> Stashed changes
                User user = userService.getUserByUserId(sup.getId());
                DeliveryMan d = deliveryManService.getDeliveryManById(s.getDelivery_man_id());
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
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouse_id());
            if (wareHouse == null) {
                System.out.println("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManager_id().equals(id) && s.getStatus().equals("approved")
                    && !s.isIsdelivery_man_Available()) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProduct_id());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
<<<<<<< Updated upstream
                Supplier sup= supplierService.getSupplierById(s.getSupplierId());
=======
                Supplier sup = supplierService.getSupplierById(s.getSupplier_id());
>>>>>>> Stashed changes
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
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouse_id());
            if (wareHouse == null) {
                System.out.println("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManager_id().equals(id) && s.getStatus().equals("cancel")) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProduct_id());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
<<<<<<< Updated upstream
                Supplier sup= supplierService.getSupplierById(s.getSupplierId());
=======
                Supplier sup = supplierService.getSupplierById(s.getSupplier_id());
>>>>>>> Stashed changes
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
            WareHouse wareHouse = wareHouseService.getWareHouseById(s.getWarehouse_id());
            if (wareHouse == null) {
                System.out.println("No warehouse for the particular supply order");
                return null;
            }

            if (wareHouse.getManager_id().equals(id) && s.getStatus().equals("pending")) {
                Map<String, Object> ma = new HashMap<>();
                Product p = productService.getProductById(s.getProduct_id());
                WareHouse ware = wareHouseService.getWareHouseById(s.getWarehouse_id());
                Supplier sup = supplierService.getSupplierById(s.getSupplier_id());
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
        WareHouse wareHouse = wareHouseService.getWareHouseById(wm.getWarehouse_id());
        List<String> p = new ArrayList<>();
        for (int i = 0; i < wareHouse.getProduct_ids().size(); i++) {
            if (wareHouse.getLowerLimits().get(i) > Integer.parseInt(wareHouse.getQuantities().get(i)) && !isSupplyOrderisCreated(wareHouse,wareHouse.getProduct_ids().get(i))) {
                p.add(wareHouse.getProduct_ids().get(i));
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
        WareHouse war = wareHouseService.getWareHouseById(wm.getWarehouse_id());
        Integer index = war.getProduct_ids().indexOf(pid);
        Integer quan = war.getHigherLimits().get(index) - Integer.parseInt(war.getQuantities().get(index));
        ;
        data.setProduct_id(pid);
        data.setSupplier_id(prod.getSupplierId());
        data.setWarehouse_id(wm.getWarehouse_id());
        data.setPayment_method("cash");
        data.setTransaction_id(null);
        data.setPickup_address(sup.getAddress());
        data.setIsdelivery_man_Available(false);
        data.setQuantity(String.valueOf(quan));

        String rsid = generateId();
        SupplyOrder supplyOrder = new SupplyOrder();
        supplyOrder.setId(rsid);
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

        try {
            addSupplyOrder(supplyOrder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        return supplyOrder;

    }
    public boolean isSupplyOrderisCreated( WareHouse w ,String Productid){
        List<SupplyOrder> so = getAllSupplyOrder();
        for(SupplyOrder s : so){
            if(s.getWarehouse_id().equals(w.getId()) && s.getProduct_id().equals(Productid)){
                return true;
            }
        }
        
        return false;
    }


    public String generateId() {
        Random rand = new Random();
        int random = rand.nextInt(1000000);
        String id = "so" + random;
        return id;
    }
}
