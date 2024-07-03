package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.project.ims.Models.GlobalDistances;

@Repository
public interface GlobalDistancesRepo extends MongoRepository<GlobalDistances, String>{
    // get distance by from and to
    public GlobalDistances findByFromAndTo(String from, String to);
}

