package com.project.ims.IServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.ims.Models.Customer;

@Service
public interface ICustomerService {

    public List<Customer> getAllCustomer();
    
    public Customer addCustomer(Customer customer);

    public Customer updateCustomer(Customer customer);

    public Customer getCustomerById(String id);

    public void deleteCustomer(String id);
}
