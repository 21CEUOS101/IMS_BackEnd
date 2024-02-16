package com.project.ims.Objects;

import com.project.ims.Models.Customer;
import com.project.ims.Models.User;

public class CustomerUserPair {
     private Customer customer;
    private User user;

    public CustomerUserPair(Customer customer, User user) {
        this.customer = customer;
        this.user = user;
    }

    // Getters and setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
