package com.project.ims.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
// imports
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.project.ims.Models.User;
import com.project.ims.Models.WareHouse_Manager;
import com.project.ims.Requests.WManager.WManagerAddRequest;
import com.project.ims.Requests.WManager.WManagerUpdateRequest;
import com.project.ims.Responses.WManagerOutput;
import com.project.ims.Services.UserService;
import com.project.ims.Services.WManagerService;
import com.project.ims.Utils.IdGenerator;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173","https://ashish2901-ims.vercel.app/","http://localhost:3000","http://localhost:3001","https://ims-frontend-eight.vercel.app/"}, allowedHeaders = "*", allowCredentials = "true")
public class WManagerController {

    private static final Logger logger = LoggerFactory.getLogger(WManagerController.class);

    // necessary dependency injections
    @Autowired
    private WManagerService wManagerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    // Controllers

    @GetMapping("/wmanager")
    public List<WManagerOutput> getAllWManagers() {

        List<WManagerOutput> output = new ArrayList();
        try {
            List<WareHouse_Manager> wareHouse_Managers = wManagerService.getAllWManager();

            for (int i = 0; i < wareHouse_Managers.size(); i++) {
                User user = userService.getUserByUserId(wareHouse_Managers.get(i).getId());
                WManagerOutput wManagerOutput = new WManagerOutput();
                wManagerOutput.setId(wareHouse_Managers.get(i).getId());
                wManagerOutput.setName(user.getName());
                wManagerOutput.setEmail(user.getEmail());
                wManagerOutput.setPhone(user.getPhone());
                wManagerOutput.setWarehouseId(wareHouse_Managers.get(i).getWarehouseId());
                output.add(wManagerOutput);
            }
//            System.out.println(output.size());
            return output;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    @GetMapping("/wmanager/{id}")
    public WManagerOutput getWManagerById(@PathVariable String id) {

        try {
            WareHouse_Manager wareHouse_Manager = wManagerService.getWManagerById(id);
            User user = userService.getUserByUserId(wareHouse_Manager.getId());
            WManagerOutput wManagerOutput = new WManagerOutput();
            wManagerOutput.setId(wareHouse_Manager.getId());
            wManagerOutput.setName(user.getName());
            wManagerOutput.setEmail(user.getEmail());
            wManagerOutput.setPhone(user.getPhone());
            wManagerOutput.setWarehouseId(wareHouse_Manager.getWarehouseId());
            return wManagerOutput;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @PostMapping("/wmanager")
    public WareHouse_Manager createWManager(@RequestBody WManagerAddRequest data) {

        String id = IdGenerator.generate("m");

        WareHouse_Manager wManager = new WareHouse_Manager();
        wManager.setId(id);
        wManager.setWarehouseId(data.getWarehouseId());

        try {
            // creating user
            createUser(data.getName(), data.getEmail(), data.getPassword(), "wmanager", data.getPhone(), id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        try {
            wManagerService.addWManager(wManager);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        return wManager;
    }

    @PostMapping("/wmanager/{id}")
    public WareHouse_Manager updateWManager(@PathVariable String id, @RequestBody WManagerUpdateRequest data) {
        WareHouse_Manager wManager = wManagerService.getWManagerById(id);
        wManager.setWarehouseId(data.getWarehouseId());
        logger.debug(wManager.getWarehouseId());
        try {
            // updating user
            updateUser(data.getName(), data.getEmail(), "wmanager", data.getPhone(), id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        try {
            wManagerService.updateWManager(wManager);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        return wManager;
    }

    @DeleteMapping("/wmanager/{id}")
    public void deleteWManager(@PathVariable String id) {

        User user = userService.getUserByUserId(id);
        try{
            // deleting user
            deleteUser(user.getUserId());
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return;
        }

        try {
            wManagerService.deleteWManager(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void createUser(String name, String email, String password, String role, String phone, String userId) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);
        user.setUserId(userId);
        userService.addUser(user);
    }

    public void updateUser(String name, String email, String role, String phone, String userId) {
        User user = userService.getUserByUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setUserId(userId);
        userService.updateUser(user);
    }

    public void deleteUser(String userId) {
        userService.deleteUserByUserId(userId);
    }
    
}
