package com.project.ims.Controllers;

// imports
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.ims.Models.Product;
import com.project.ims.Requests.ProductAddRequest;
import com.project.ims.Services.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

    // necessary dependency injections
    @Autowired
    private ProductService productService;

    // controllers

    @GetMapping("/product")
    public List<Product> getProducts() {
        
        try{
            List<Product> products = productService.getAllProduct();
            return products;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

    }

    @GetMapping("/product/{id}")
    public Product getProductById(@PathVariable String id) {
        try{
            Product product = productService.getProductById(id);
            return product;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping("/product")
    public Product addProduct(@RequestBody ProductAddRequest data) {

        String id = generateId();
        Product product = new Product();
        product.setId(id);
        product.setName(data.getName());
        product.setPrice(data.getPrice());
        product.setExpiry_date(data.getExpiry_date());
        product.setSupplier_id(data.getSupplier_id());

        try{
            productService.addProduct(product);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return product;
    }

    @PostMapping("/product/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody ProductAddRequest data) {
        Product product = productService.getProductById(id);
        product.setName(data.getName());
        product.setPrice(data.getPrice());
        product.setExpiry_date(data.getExpiry_date());
        product.setSupplier_id(data.getSupplier_id());

        try{
            productService.updateProduct(product);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

        return product;
    }

    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable String id) {
        try{
            productService.deleteProduct(id);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public String generateId()
    {
        Random rand = new Random();
        String id = "p" + rand.nextInt(100000);
        return id;
    }

}
