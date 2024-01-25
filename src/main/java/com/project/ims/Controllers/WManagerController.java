package com.project.ims.Controllers;

// imports
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.Models.User;
import com.project.ims.Models.WareHouse_Manager;
import com.project.ims.Requests.WManagerAddRequest;
import com.project.ims.Services.UserService;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

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
        wManager.setWarehouse_id(data.getWarehouse_id());

        try{
            // creating user
            createUser(data.getName() , data.getEmail() , data.getPassword() , "wmanager", data.getPhone() , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

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
        wManager.setWarehouse_id(data.getWarehouse_id());

        try{
            // updating user
            updateUser(data.getName() , data.getEmail() , data.getPassword() , "wmanager" , data.getPhone() , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

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
            // deleting user
            deleteUser(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

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

    public void createUser(String name, String email, String password, String role, String phone, String userId) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);
        user.setUserId(userId);

        try {
            userService.addUser(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void updateUser(String name, String email, String password, String role, String phone, String userId) {
        User user = userService.getUserByUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);
        user.setUserId(userId);

        try {
            userService.updateUser(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteUser(String userId) {
        try {
            userService.deleteUserByUserId(userId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
