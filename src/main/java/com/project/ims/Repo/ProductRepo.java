package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.Product;

@Repository
public interface ProductRepo extends MongoRepository<Product, String>{
    
}
