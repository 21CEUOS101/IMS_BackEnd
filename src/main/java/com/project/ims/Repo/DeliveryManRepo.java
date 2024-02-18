package com.project.ims.Repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.DeliveryMan;

@Repository
public interface DeliveryManRepo extends MongoRepository<DeliveryMan, String>{
    public List<DeliveryMan> findByWarehouseId(String warehouseId);
}
