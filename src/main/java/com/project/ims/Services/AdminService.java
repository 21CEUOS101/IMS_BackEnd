package com.project.ims.Services;

// imports 
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IAdminService;
import com.project.ims.Models.Admin;
import com.project.ims.Repo.AdminRepo;

@Component
@Service
public class AdminService implements IAdminService {

    // necessary dependency Injections
    @Autowired
    private AdminRepo adminRepo;


    // Services


    @Override
    public Admin getAdminById(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

        return adminRepo.findById(id).orElse(null);
    }

    @Override
    public Admin addAdmin(Admin admin) {
        
        if(admin == null)
        {
            throw new RuntimeException("Admin data shouldn't be null");
        }
        else if(admin.getId() == null || admin.getId().isEmpty())
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

        // checking if admin data is null or not
        if (admin == null)
        {
            throw new RuntimeException("Admin data shouldn't be null");
        }

        return adminRepo.save(admin);
    }

    @Override
    public void deleteAdmin(String id) {

        // checking if id is valid or not
        if (id != null && adminRepo.existsById(id))
        {
            adminRepo.deleteById(id);
        }
        else
        {
            throw new RuntimeException("Admin doesn't exist");
        }
    }


    // get all admins
    @Override
    public List<Admin> getAllAdmin() {
        return adminRepo.findAll();
    }
    
}
