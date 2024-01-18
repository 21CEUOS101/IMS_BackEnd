package com.project.ims.Repo;

// imports
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.W2WOrder;

@Repository
public interface W2WOrderRepo extends MongoRepository<W2WOrder, String>{
    public List<W2WOrder> findByOrderId(String orderId);
}
