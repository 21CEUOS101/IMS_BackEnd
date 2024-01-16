package com.project.ims.Repo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.ReturnOrder;

@Repository
public interface ReturnOrderRepo extends MongoRepository<ReturnOrder, String>{
    public List<ReturnOrder> findAllByCustomerId(String customerId);
    public List<ReturnOrder> findAllByWarehouseId(String warehouseId);
}
