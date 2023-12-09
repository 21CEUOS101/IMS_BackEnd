package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.Supplier;

@Repository
public interface SupplierRepo extends MongoRepository<Supplier, String>{
    
}
