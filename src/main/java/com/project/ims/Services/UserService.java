package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.ims.Models.User;
import com.project.ims.Repo.UserRepo;

@Service
public class UserService {

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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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

        try {
            User newUser = userRepo.save(user);
            return newUser;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
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
        
        User check = userRepo.findByEmail(user.getEmail());

        if (check.getId() != user.getId())
        {
            throw new RuntimeException("User Email Already Exists");
        }

        try {
            User newUser = userRepo.save(user);
            return newUser;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    // delete User
    public void deleteUser(String id) {

        if (id == null) {
            throw new RuntimeException("Id shouldn't be null");
        } else if (!userRepo.existsById(id)) {
            throw new RuntimeException("User ID does not exist");
        }

        try {
            userRepo.deleteById(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // delete User by email
    public void deleteUserByEmail(String email) {

        if (email == null) {
            throw new RuntimeException("Email shouldn't be null");
        } else if (userRepo.findByEmail(email) == null) {
            throw new RuntimeException("User with email " + email + " does not exist");
        }

        try {
            userRepo.deleteByEmail(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // delete User by userId
    public void deleteUserByUserId(String userId) {

        if (userId == null) {
            throw new RuntimeException("User ID shouldn't be null");
        } else if (userRepo.findByUserId(userId) == null) {
            throw new RuntimeException("User with userId " + userId + " does not exist");
        }

        try {
            userRepo.deleteByUserId(userId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
}
