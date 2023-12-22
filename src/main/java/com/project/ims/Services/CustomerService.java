package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.ims.IServices.ICustomerService;
import com.project.ims.Models.Customer;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
@Service
public class CustomerService implements ICustomerService {
    
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
    public Customer getCustomerById(String id) {
        return customerRepo.findById(id).orElse(null);
    }

    @Override
    public Customer addCustomer(Customer customer) {
        
        if(customerRepo.existsById(customer.getId()))
        {
            throw new RuntimeException("Customer with id " + customer.getId() + " already exists");
        }
        else if (customer.getId() == null || customer.getId().isEmpty())
        {
            throw new RuntimeException("Customer ID cannot be empty");
        }
        else if (customer.getId().charAt(0) != 'c')
        {
            throw new RuntimeException("Customer ID must start with 'c'");
        }
        
        return customerRepo.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return customerRepo.save(customer);
    }

    @Override
    public void deleteCustomer(String id) {

        if (customerRepo.existsById(id))
        {
            throw new RuntimeException("Customer with id " + id + " does not exist");
        }

        customerRepo.deleteById(id);
    }

    @Override
    public List<Customer> getAllCustomer() {
        return customerRepo.findAll();
    }
    
}
