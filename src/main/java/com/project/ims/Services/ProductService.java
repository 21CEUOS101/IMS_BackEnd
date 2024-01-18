package com.project.ims.Services;

import java.util.ArrayList;
import java.util.HashMap;
// imports
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IProductService;
import com.project.ims.Models.GlobalProducts;
import com.project.ims.Models.Product;
import com.project.ims.Repo.ProductRepo;

@Service
public class ProductService implements IProductService {

    // necessary dependency Injections
    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private GPService gpService;

    // Services

    @Override
    public Product getProductById(String id) {
        return productRepo.findById(id).orElse(null);
    }

    @Override
    public Product addProduct(Product product) {

        if (product.getId() == null || product.getId().isEmpty())
        {
            throw new RuntimeException("Product ID cannot be empty");
        }
        else if (productRepo.existsById(product.getId()))
        {
            throw new RuntimeException("Product ID already exists");
        }
        else if (product.getId().charAt(0) != 'p')
        {
            throw new RuntimeException("Product ID must start with 'p'");
        }

        try{
            List<String> warehouses = new ArrayList<>();
            List<String> quantities = new ArrayList<>();

            gpService.add(product.getId(), product.getName(),warehouses , quantities);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error in adding product to global products");
        }

        return productRepo.save(product);
    }

    @Override
    public Product updateProduct(Product product) {

        if (product.getId() == null || product.getId().isEmpty())
        {
            throw new RuntimeException("Product ID cannot be empty");
        }
        else if (!productRepo.existsById(product.getId()))
        {
            throw new RuntimeException("Product with id " + product.getId() + " does not exist");
        }

        // updating global product if name is changed
        GlobalProducts globalProducts = gpService.getById(product.getId());

        if(globalProducts == null)
        {
            throw new RuntimeException("Product with id " + product.getId() + " does not exist");
        }

        if(!globalProducts.getName().equals(product.getName()))
        {
            globalProducts.setName(product.getName());
            gpService.update(globalProducts.getId(), globalProducts.getName(), globalProducts.getWarehouses() , globalProducts.getQuantities());
        }

        return productRepo.save(product);
    }

    @Override
    public void deleteProduct(String id) {

        if (id == null)
        {
            throw new RuntimeException("Product ID cannot be empty");
        }
        else if (!productRepo.existsById(id))
        {
            throw new RuntimeException("Product with id " + id + " does not exist");
        }

        // deleting from global products
        try{
            gpService.delete(id);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error in deleting product from global products");
        }

        productRepo.deleteById(id);
    }

    public List<Product> getAllProduct() {
        return productRepo.findAll();
    }
    
}
