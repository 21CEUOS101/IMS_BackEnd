package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.Admin;

@Repository
public interface AdminRepo extends MongoRepository<Admin, String>{
    
}
