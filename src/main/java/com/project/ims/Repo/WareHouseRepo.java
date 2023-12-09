package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.WareHouse;

@Repository
public interface WareHouseRepo extends MongoRepository<WareHouse, String>{
    
}
