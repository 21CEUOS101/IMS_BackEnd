package com.project.ims.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.ims.Models.User;
import com.project.ims.Repo.UserRepo;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // necessary dependency injections
    @Autowired
    private UserRepo userRepo;

    // services

    // get All Users
    public List<User> getAllUsers() {
        try {
            List<User> users = userRepo.findAll();
            return users;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    
    // get User by id
    public User getUserById(String id) {

        if (id == null) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!userRepo.existsById(id)) {
            throw new RuntimeException("User ID does not exist");
        }

        try {
            User user = userRepo.findById(id).get();
            return user;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    // get User by email
    public User getUserByEmail(String email) {

        if (email == null) {
            throw new RuntimeException("Email shouldn't be null");
        } else if (userRepo.findByEmail(email) == null) {
            throw new RuntimeException("User with email " + email + " does not exist");
        }

        try {
            User user = userRepo.findByEmail(email);
            return user;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    // get User by userId
    public User getUserByUserId(String userId) {

        if (userId == null) {
            throw new RuntimeException("User ID shouldn't be null");
        } else if (userRepo.findByUserId(userId) == null) {
            throw new RuntimeException("User with userId " + userId + " does not exist");
        }

        try {
            User user = userRepo.findByUserId(userId);
            return user;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    // add User
    public User addUser(User user) {

        if(user == null)
        {
            throw new RuntimeException("User cannot be null");
        }
        else if(userRepo.findByEmail(user.getEmail()) != null)
        {
            throw new RuntimeException("User already exists");
        }

        User newUser = userRepo.save(user);
        return newUser;
    }

    // update User
    public User updateUser(User user) {
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }
        else if(userRepo.findById(user.getId()).orElse(null) == null)
        {
            throw new RuntimeException("User not Exists");
        }
        
        // User check = userRepo.findByEmail(user.getEmail());

        // if (check.getId() != user.getId())
        // {
        //     throw new RuntimeException("User Email Already Exists");
        // }
        User newUser = userRepo.save(user);
        return newUser;
    }
    
    // delete User
    public void deleteUser(String id) {

        if (id == null) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!userRepo.existsById(id)) {
            throw new RuntimeException("User ID does not exist");
        }

        userRepo.deleteById(id);
    }

    // delete User by email
    public void deleteUserByEmail(String email) {

        if (email == null) {
            throw new RuntimeException("Email shouldn't be null");
        } else if (userRepo.findByEmail(email) == null) {
            throw new RuntimeException("User with email " + email + " does not exist");
        }

        userRepo.deleteByEmail(email);
    }

    // delete User by userId
    public void deleteUserByUserId(String userId) {

        if (userId == null) {
            throw new RuntimeException("User ID shouldn't be null");
        } else if (userRepo.findByUserId(userId) == null) {
            throw new RuntimeException("User with userId " + userId + " does not exist");
        }

        userRepo.deleteByUserId(userId);
    }
    
}
