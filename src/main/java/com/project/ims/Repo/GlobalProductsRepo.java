package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.project.ims.Models.GlobalProducts;

@Repository
public interface GlobalProductsRepo extends MongoRepository<GlobalProducts, String>{

}
