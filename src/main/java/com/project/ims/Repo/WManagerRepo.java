package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.WareHouse_Manager;

@Repository
public interface WManagerRepo extends MongoRepository<WareHouse_Manager, String>{
    
}
