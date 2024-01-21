package com.project.ims.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.ims.Models.User;


@Repository
public interface UserRepo extends MongoRepository<User, String>{
    public User findByEmail(String email);

    public User findByUserId(String userId);
    
    public void deleteByUserId(String userId);

    public void deleteByEmail(String email);
}
