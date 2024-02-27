package com.project.ims.IServices;
import java.util.List;
import org.springframework.stereotype.Service;
import com.project.ims.Models.Admin;
import com.project.ims.Models.Order;
import com.project.ims.Responses.RecentSales;

@Service
public interface IAdminService {

    public List<Admin> getAllAdmin();
    public Admin getAdminById(String id);
    public Admin addAdmin(Admin admin);
    public Admin updateAdmin(Admin admin);
    public void deleteAdmin(String id);
    public List<RecentSales> getRecentSales();
}
