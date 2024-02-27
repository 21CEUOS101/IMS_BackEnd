package com.project.ims.Repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.Order;

@Repository
public interface OrderRepo extends MongoRepository<Order, String>{
    public List<Order> findByCustomerId(String customerId);

    // get top 10 orders by date which are delivered
    @Query("{ 'status' : 'delivered' }")
    public List<Order> findTop10DeliveredOrders(Pageable pageable);
}
