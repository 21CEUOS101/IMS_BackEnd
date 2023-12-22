package com.project.ims.Controllers;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.IServices.IAdminService;
import com.project.ims.IServices.ICustomerService;
import com.project.ims.IServices.IDeliveryManService;
import com.project.ims.IServices.IOrderService;
import com.project.ims.IServices.IProductService;
import com.project.ims.IServices.ISupplierService;
import com.project.ims.IServices.IWManagerService;
import com.project.ims.IServices.IWareHouseService;
import com.project.ims.Models.WareHouse_Manager;
import com.project.ims.Requests.WManagerAddRequest;
import com.project.ims.Services.WManagerService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class WManagerController {

    @Autowired
    private WManagerService wManagerService;

    @GetMapping("/wmanager")
    public List<WareHouse_Manager> getAllWManagers() {
        return wManagerService.getAllWManager();
    }
    
    @GetMapping("/wmanager/{id}")
    public WareHouse_Manager getWManagerById(@PathVariable String id) {
        return wManagerService.getWManagerById(id);
    }
    

    @PostMapping("/wmanager")
    public WareHouse_Manager createWManager(@RequestBody WManagerAddRequest data) {
        WareHouse_Manager wManager = new WareHouse_Manager();
        Random rand = new Random();
        String id = "wm" + String.valueOf(rand.nextInt(1000000));
        wManager.setId(id);
        wManager.setName(data.getName());
        wManager.setEmail(data.getEmail());
        wManager.setPassword(data.getPassword());
        wManager.setPhone(data.getPhone());
        wManager.setWarehouse_id(data.getWarehouse_id());
        wManagerService.addWManager(wManager);
        return wManager;
    }

    @PostMapping("/wmanager/{id}")
    public WareHouse_Manager updateWManager(@PathVariable String id, @RequestBody WManagerAddRequest data) {
        WareHouse_Manager wManager = wManagerService.getWManagerById(id);
        wManager.setName(data.getName());
        wManager.setEmail(data.getEmail());
        wManager.setPassword(data.getPassword());
        wManager.setPhone(data.getPhone());
        wManager.setWarehouse_id(data.getWarehouse_id());
        wManagerService.addWManager(wManager);
        return wManager;
    }

    @DeleteMapping("/wmanager/{id}")
    public void deleteWManager(@PathVariable String id) {
        wManagerService.deleteWManager(id);
    }
}
