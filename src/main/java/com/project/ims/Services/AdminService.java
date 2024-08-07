package com.project.ims.Services;

import java.util.ArrayList;
// imports 
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IAdminService;
import com.project.ims.Models.Admin;
import com.project.ims.Models.Customer;
import com.project.ims.Models.Order;
import com.project.ims.Models.Product;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.UserRepo;
import com.project.ims.Responses.RecentSales;

@Service
public class AdminService implements IAdminService {

    // necessary dependency Injections
    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OrderRepo orderRepository;

    @Autowired
    private ProductRepo productRepo;

    // Services
    @Override
    public Admin getAdminById(String id) {

        if (id == null) {
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

    // get recent sales
    @Override
    public List<RecentSales> getRecentSales() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "delivered_date_time"));
        List<Order> orders = orderRepository.findTop10DeliveredOrders(pageable);

        List<RecentSales> recentSales = new ArrayList<>();

        for(int i=0;i<orders.size();i++)
        {
            RecentSales recentSale = new RecentSales();
            recentSale.setOrderId(orders.get(i).getId());
            recentSale.setCustomerId(orders.get(i).getCustomerId());
            recentSale.setDate(orders.get(i).getDelivered_date_time());

            Integer totalAmount = Integer.parseInt(orders.get(i).getTotal_amount());
            recentSale.setTotalAmount(totalAmount);

            // calculate profit for each order
            
            Integer profit = 0;

            // take one product profit percentage because all products are same in one order

            Product product = productRepo.findById(orders.get(i).getProduct_id()).orElse(null);

            Integer profitPercentage = product.getProfit();
            Integer priceOfProduct = product.getWhole_sale_price();

            Integer profitPerProduct = (profitPercentage * priceOfProduct) / 100;

            profit = profitPerProduct * Integer.parseInt(orders.get(i).getQuantity());

            recentSale.setProfit(profit);

            recentSales.add(recentSale);
        }

        return recentSales;
    }
    
}
