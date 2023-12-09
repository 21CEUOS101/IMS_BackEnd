package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.Order;

@Repository
public interface OrderRepo extends MongoRepository<Order, String>{
    
}
