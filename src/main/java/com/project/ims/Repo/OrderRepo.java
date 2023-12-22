package com.project.ims.Repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.Order;

@Repository
public interface OrderRepo extends MongoRepository<Order, String>{
    public List<Order> findByCustomerId(String customerId);
}
