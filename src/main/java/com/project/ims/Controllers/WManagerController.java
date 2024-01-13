package com.project.ims.Controllers;

// imports
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
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

    // necessary dependency injections
    @Autowired
    private WManagerService wManagerService;

    // Controllers 

    @GetMapping("/wmanager")
    public List<WareHouse_Manager> getAllWManagers() {

        try{
            List<WareHouse_Manager> wareHouse_Managers = wManagerService.getAllWManager();
            return wareHouse_Managers;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
        
    }
    
    @GetMapping("/wmanager/{id}")
    public WareHouse_Manager getWManagerById(@PathVariable String id) {

        try{
            WareHouse_Manager wareHouse_Manager = wManagerService.getWManagerById(id);
            return wareHouse_Manager;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }
    

    @PostMapping("/wmanager")
    public WareHouse_Manager createWManager(@RequestBody WManagerAddRequest data) {

        String id = generateId();

        WareHouse_Manager wManager = new WareHouse_Manager();
        wManager.setId(id);
        wManager.setName(data.getName());
        wManager.setEmail(data.getEmail());
        wManager.setPassword(data.getPassword());
        wManager.setPhone(data.getPhone());
        wManager.setWarehouse_id(data.getWarehouse_id());

        try{
            wManagerService.addWManager(wManager);
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }

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

        try{
            wManagerService.updateWManager(wManager);
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return wManager;
    }

    @DeleteMapping("/wmanager/{id}")
    public void deleteWManager(@PathVariable String id) {
        try{
            wManagerService.deleteWManager(id);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public String generateId() {
        // id generation
        Random rand = new Random();
        String id = 'm' + String.valueOf(rand.nextInt(1000000));

        return id;
    }
}
