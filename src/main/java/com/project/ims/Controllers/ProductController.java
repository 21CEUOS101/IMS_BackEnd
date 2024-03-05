package com.project.ims.Controllers;

import java.util.ArrayList;
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

import com.project.ims.Models.GlobalProducts;
import com.project.ims.Models.Product;
import com.project.ims.Requests.ProductAddRequest;
import com.project.ims.Services.GPService;
import com.project.ims.Services.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

    // necessary dependency injections
    @Autowired
    private ProductService productService;

    @Autowired
    private GPService gpService;

    // controllers

    @GetMapping("/global-product")
    public List<GlobalProducts> getGlobalProducts() {
        
        List<GlobalProducts> output = new ArrayList<>();
        try{
            List<Product> products = productService.getAllProduct();

            for(int i=0;i<products.size();i++)
            {
                GlobalProducts globalProducts = gpService.getById(products.get(i).getId());
                output.add(globalProducts);
            }

            return output;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

    }

    @GetMapping("/global-product/{id}")
    public GlobalProducts getGlobalProductById(@PathVariable String id) {
        try {
            GlobalProducts product = gpService.getById(id);
            return product;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    @GetMapping("/product")
    public List<Product> getProducts() {
        try {
            return productService.getAllProduct();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/product/{id}")
    public Product getProductById(@PathVariable String id) {
        try {
            return productService.getProductById(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping("/product")
    public Product addProduct(@RequestBody ProductAddRequest data) {

        String id = generateId();
        Product product = new Product();
        Integer pr = data.getWhole_sale_price() +( data.getWhole_sale_price()*data.getProfit() )/100+ (data.getWhole_sale_price()*data.getTax())/100;
        String prString = String.valueOf(pr);
        product.setId(id);
        product.setName(data.getName());
        product.setPrice(prString);
        product.setExpiry_date(data.getExpiry_date());
        product.setSupplierId(data.getSupplierId());
        product.setTax(data.getTax());
        product.setWhole_sale_price(data.getWhole_sale_price());
        product.setProfit(data.getProfit());
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
        Integer pr = data.getWhole_sale_price() +( data.getWhole_sale_price()*data.getProfit() )/100+ (data.getWhole_sale_price()*data.getTax())/100;
        String prString = String.valueOf(pr);
        product.setPrice(prString);
        product.setExpiry_date(data.getExpiry_date());
        product.setSupplierId(data.getSupplierId());
        product.setTax(data.getTax());
        product.setWhole_sale_price(data.getWhole_sale_price());
        product.setProfit(data.getProfit());
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
