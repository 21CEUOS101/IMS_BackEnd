package com.project.ims.Controllers;

// imports
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.Models.JwtRequest;
import com.project.ims.Models.JwtResponse;
import com.project.ims.Models.User;
import com.project.ims.Repo.UserRepo;
import com.project.ims.Requests.ChangePassword;
import com.project.ims.Security.JwtHelper;
import com.project.ims.Services.CustomUserDetailService;

import org.slf4j.Logger;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;


    @Autowired
    private JwtHelper helper;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        System.out.println("Login Request : " + request);
        this.doAuthenticate(request.getUsername(), request.getPassword());


        User user = userDetailsService.loadUserByUsername(request.getUsername());
        String token = this.helper.generateToken(user);

        JwtResponse response = JwtResponse.builder()
                .token(token)
                .username(user.getUsername()).build();
        
        // append id and role to response
        response.setId(user.getUserId());
        response.setRole(user.getRole());
        response.setSuccess("true");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String username, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }

    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }

    @PostMapping("/register")
    public User createUser(@RequestBody User user) {

        System.out.println(user);

        User newUser = new User();
        newUser.setUserId(user.getUserId());
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole(user.getRole());
        newUser.setPhone(user.getPhone());

        try {
            userRepo.save(newUser);

            return newUser;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword data) {

        User user1 = userRepo.findByEmail(data.getEmail());

        if (user1 != null) {
            if (passwordEncoder.matches(data.getOldPassword(), user1.getPassword())) {
                user1.setPassword(passwordEncoder.encode(data.getNewPassword()));
                userRepo.save(user1);
                return new ResponseEntity<>("Password Changed Successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Old Password is Incorrect", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        }
    }

    
}

