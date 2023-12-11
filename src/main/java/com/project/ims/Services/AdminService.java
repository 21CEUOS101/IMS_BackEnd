package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.ims.IServices.IAdminService;
import com.project.ims.Models.Admin;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
public class AdminService implements IAdminService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private DeliveryManRepo deliveryManRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private SupplierRepo supplierRepo;

    @Autowired
    private WareHouseRepo wareHouseRepo;

    @Autowired
    private WManagerRepo wManagerRepo;


    @Override
    public Admin getAdminById(String id) {
        return adminRepo.findById(id).orElse(null);
    }

    @Override
    public Admin addAdmin(Admin admin) {

        if(admin.getId() == null || admin.getId().isEmpty())
        {
            throw new RuntimeException("Admin ID cannot be empty");
        }
        else if(adminRepo.existsById(admin.getId()))
        {
            throw new RuntimeException("Admin ID already exists");
        }
        else if (admin.getId().charAt(0) != 'a')
        {
            throw new RuntimeException("Admin ID must start with 'a'");
        }

        return adminRepo.save(admin);
    }

    @Override
    public Admin updateAdmin(Admin admin) {
        return adminRepo.save(admin);
    }

    @Override
    public void deleteAdmin(String id) {

        if (adminRepo.existsById(id))
        {
            adminRepo.deleteById(id);
        }
    }

    @Override
    public List<Admin> getAllAdmin() {
        return adminRepo.findAll();
    }
    
}
