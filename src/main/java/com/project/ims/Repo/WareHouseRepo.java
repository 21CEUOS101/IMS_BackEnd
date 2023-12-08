package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WareHouseRepo extends MongoRepository<WareHouseRepo, String>{
    
}
