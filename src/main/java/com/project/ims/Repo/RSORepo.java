package com.project.ims.Repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.ReturnSupplyOrder;

@Repository
public interface RSORepo  extends MongoRepository<ReturnSupplyOrder, String>{
    public List<ReturnSupplyOrder> findBySupplierId(String supplierId);
}
