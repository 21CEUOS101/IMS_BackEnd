package com.project.ims.IServices;

import org.springframework.stereotype.Service;

import com.project.ims.Models.Customer;

@Service
public interface ICustomerService {
    
    public Customer addCustomer(Customer customer);

    public Customer updateCustomer(Customer customer);

    public Customer getCustomerById(int id);

    public void deleteCustomer(int id);
}
