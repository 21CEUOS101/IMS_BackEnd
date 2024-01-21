package com.project.ims.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.ims.Models.User;
import com.project.ims.Repo.UserRepo;

@Service
public class CustomUserDetailService implements UserDetailsService {
    
    @Autowired
    private UserRepo userRepo;

    @Override
    public User loadUserByUsername(String id) throws UsernameNotFoundException {
        
        if(id == null)
            throw new UsernameNotFoundException("User not found");

        User user = userRepo.findByEmail(id);

        return user;
    }
    
}
