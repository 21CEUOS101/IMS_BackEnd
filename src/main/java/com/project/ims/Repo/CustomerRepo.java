package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.Customer;

@Repository
public interface CustomerRepo extends MongoRepository<Customer, String>{
    
}
