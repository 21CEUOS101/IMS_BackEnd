package com.project.ims.Controllers;

// imports
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.Models.Admin;
import com.project.ims.Models.User;
import com.project.ims.Requests.AdminAddRequest;
import com.project.ims.Services.AdminService;
import com.project.ims.Services.UserService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000" , allowedHeaders = "*" , allowCredentials = "true")
public class AdminController {

    // necessary dependency injections
    @Autowired
    private AdminService adminService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    // controllers

    // get all admins
    @GetMapping("/admin")
    public List<Admin> getAllAdmins() {
        try{
            List<Admin> admin = adminService.getAllAdmin();
            return admin;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // get admin by id
    @GetMapping("/admin/{id}")
    public Admin getAdminById(@PathVariable("id") String id) {
        try{
            Admin admin = adminService.getAdminById(id);
            return admin;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // create admin
    @PostMapping("/admin")
    public Admin createAdmin(@RequestBody AdminAddRequest data) {

        String id = generateId();

        Admin admin = new Admin();
        admin.setId(id);
        admin.setPhone(data.getPhone());

        try{
            // creating user
            createUser(data.getName() , data.getEmail() , data.getPassword() , "admin" , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        try{
            // saving admin in database
            adminService.addAdmin(admin);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return admin;
    }
    
    @PostMapping("/admin/{id}")
    public Admin updateAdmin(@PathVariable("id") String id, @RequestBody AdminAddRequest data) {

        Admin admin = adminService.getAdminById(id);
        admin.setPhone(data.getPhone());

        try{
            // updating user
            updateUser(data.getName() , data.getEmail() , data.getPassword() , "admin" , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        try {
            // updating admin in database
            adminService.updateAdmin(admin);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return admin;
    }

    @DeleteMapping("/admin/{id}")
    public void deleteAdmin(@PathVariable("id") String id) {

        try{
            // deleting user
            deleteUser(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        try{
            adminService.deleteAdmin(id);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String generateId() {
        // id generation
        Random rand = new Random();
        String id = 'a' + String.valueOf(rand.nextInt(1000000));

        return id;
    }

    public void createUser(String name, String email, String password, String role, String userId) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setUserId(userId);

        try {
            userService.addUser(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void updateUser(String name, String email, String password, String role, String userId) {
        User user = userService.getUserByUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
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
