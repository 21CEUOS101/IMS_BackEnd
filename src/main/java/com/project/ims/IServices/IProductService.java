package com.project.ims.IServices;

import org.springframework.stereotype.Service;

import com.project.ims.Models.Product;

@Service
public interface IProductService {
        
        public Product getProductById(int id);
        
        public Product addProduct(Product product);
        
        public Product updateProduct(Product product);
    
        public void deleteProduct(int id);
}
