package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.SupplyOrder;

@Repository
public interface SupplyOrderRepo extends MongoRepository<SupplyOrder, String>{
    
}
