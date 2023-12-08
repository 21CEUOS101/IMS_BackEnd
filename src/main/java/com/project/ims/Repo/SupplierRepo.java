package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepo extends MongoRepository<SupplierRepo, String>{
    
}
