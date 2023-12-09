package com.project.ims.IServices;

import org.springframework.stereotype.Service;

import com.project.ims.Models.Admin;

@Service
public interface IAdminService {
    
    public Admin getAdminById(String id);
    
    public Admin addAdmin(Admin admin);
    
    public Admin updateAdmin(Admin admin);

    public void deleteAdmin(String id);

}
