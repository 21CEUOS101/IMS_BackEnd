package com.project.ims.Controllers;

import java.util.ArrayList;
// imports
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.Models.Admin;
import com.project.ims.Models.User;
import com.project.ims.Requests.Admin.AdminAddRequest;
import com.project.ims.Requests.Admin.AdminUpdateRequest;
import com.project.ims.Responses.AdminOutput;
import com.project.ims.Responses.RecentSales;
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
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
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
    public List<AdminOutput> getAllAdmins() {
        try {
            List<AdminOutput> admins = new ArrayList<>();
            List<Admin> admin = adminService.getAllAdmin();

            // also get user details
            for(int i=0;i<admin.size();i++)
            {
                User user = userService.getUserByUserId(admin.get(i).getId());
                AdminOutput adminOutput = new AdminOutput();
                adminOutput.setId(admin.get(i).getId());
                adminOutput.setName(user.getName());
                adminOutput.setEmail(user.getEmail());
                adminOutput.setPassword(user.getPassword());
                adminOutput.setPhone(user.getPhone());
                admins.add(adminOutput);
            }

            return admins;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // get admin by id
    @GetMapping("/admin/{id}")
    public AdminOutput getAdminById(@PathVariable("id") String id) {
        try {
            AdminOutput adminOutput = new AdminOutput();
            Admin admin = adminService.getAdminById(id);
            User user = userService.getUserByUserId(admin.getId());

            adminOutput.setId(admin.getId());
            adminOutput.setName(user.getName());
            adminOutput.setEmail(user.getEmail());
            adminOutput.setPassword(user.getPassword());
            adminOutput.setPhone(user.getPhone());

            return adminOutput;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // analytics for admin
    @GetMapping("/admin/analytics/recent-sales")
    public List<RecentSales> getRecentSales() {
        try {
            return adminService.getRecentSales();
        } catch (Exception e) {
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

        try{
            // creating user
            createUser(data.getName() , data.getEmail() , data.getPassword() , "admin" , data.getPhone() , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
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
    public Admin updateAdmin(@PathVariable("id") String id, @RequestBody AdminUpdateRequest data) {

        Admin admin = adminService.getAdminById(id);

        try{
            // updating user
            updateUser(data.getName() , data.getEmail() ,data.getPhone() , "admin" , id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
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

        User user = userService.getUserByUserId(id);
        try{
            // deleting user
            deleteUser(user.getUserId());
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return;
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
    
    public void updateUser(String name, String email, String phone, String role, String userId) {
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
