package com.project.ims.Services;

// imports
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.ICustomerService;
import com.project.ims.Models.Customer;
import com.project.ims.Repo.CustomerRepo;

@Component
@Service
public class CustomerService implements ICustomerService {

    // necessary dependency Injections
    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public Customer getCustomerById(String id) {

        // checking if id is null or not
        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

        return customerRepo.findById(id).orElse(null);
    }

    @Override
    public Customer addCustomer(Customer customer) {
        
        // checking if customer data is null or not
        if (customer == null)
        {
            throw new RuntimeException("Customer data shouldn't be null");
        }
        else if (customer.getId() == null || customer.getId().isEmpty())
        {
            throw new RuntimeException("Customer ID cannot be empty");
        }
        else if(customerRepo.existsById(customer.getId()))
        {
            throw new RuntimeException("Customer with id " + customer.getId() + " already exists");
        }
        else if (customer.getId().charAt(0) != 'c')
        {
            throw new RuntimeException("Customer ID must start with 'c'");
        }
        
        return customerRepo.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {

        // checking if customer data is null or not
        if (customer == null)
        {
            throw new RuntimeException("Customer data shouldn't be null");
        }

        return customerRepo.save(customer);
    }

    @Override
    public void deleteCustomer(String id) {

        // checking if id is null or not
        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

        customerRepo.deleteById(id);
    }

    // get all customers
    @Override
    public List<Customer> getAllCustomer() {
        return customerRepo.findAll();
    }
    
}
