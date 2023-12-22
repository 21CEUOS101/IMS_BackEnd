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
import com.project.ims.Models.Admin;
import com.project.ims.Requests.AdminAddRequest;
import com.project.ims.Services.AdminService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/{id}")
    public Admin getAdminById(@PathVariable("id") String id) {
        Admin admin = adminService.getAdminById(id);
        return admin;
    }

    @GetMapping("/admin")
    public List<Admin> getAllAdmins() {
        List<Admin> admin = adminService.getAllAdmin();
        return admin;
    }

    @PostMapping("/admin")
    public Admin createAdmin(@RequestBody AdminAddRequest data) {
        
        Random rand = new Random();
        Admin admin = new Admin();
        String id = 'a' + String.valueOf(rand.nextInt(1000000));
        admin.setId(id);
        admin.setName(data.getName());
        admin.setEmail(data.getEmail());
        admin.setPassword(data.getPassword());
        admin.setPhone(data.getPhone());
        adminService.addAdmin(admin);
        return admin;
    }
    
    @PostMapping("/admin/{id}")
    public Admin updateAdmin(@PathVariable("id") String id, @RequestBody AdminAddRequest data) {
        Admin admin = adminService.getAdminById(id);
        admin.setName(data.getName());
        admin.setEmail(data.getEmail());
        admin.setPassword(data.getPassword());
        admin.setPhone(data.getPhone());
        adminService.updateAdmin(admin);
        return admin;
    }

    @DeleteMapping("/admin/{id}")
    public void deleteAdmin(@PathVariable("id") String id) {
        adminService.deleteAdmin(id);
    }
}
