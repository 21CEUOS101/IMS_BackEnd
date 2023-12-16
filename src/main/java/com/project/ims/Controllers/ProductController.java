package com.project.ims.Controllers;

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

import com.project.ims.IServices.IAdminService;
import com.project.ims.IServices.ICustomerService;
import com.project.ims.IServices.IDeliveryManService;
import com.project.ims.IServices.IOrderService;
import com.project.ims.IServices.IProductService;
import com.project.ims.IServices.ISupplierService;
import com.project.ims.IServices.IWManagerService;
import com.project.ims.IServices.IWareHouseService;
import com.project.ims.Models.Product;
import com.project.ims.Requests.ProductAddRequest;

@RestController
@RequestMapping("/api")
public class ProductController {
    
    @Autowired
    private IAdminService adminService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IDeliveryManService deliveryManService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IProductService productService;

    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private IWareHouseService wareHouseService;

    @Autowired
    private IWManagerService wManagerService;

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.getAllProduct();
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping("/products")
    public Product addProduct(@RequestBody ProductAddRequest data) {
        Product product = new Product();
        Random rand = new Random();
        product.setId("p" + rand.nextInt(100000));
        product.setName(data.getName());
        product.setPrice(data.getPrice());
        product.setDescription(data.getDescription());
        product.setCategory(data.getCategory());
        product.setCompany_name(data.getCompany_name());
        product.setExpiry_date(data.getExpiry_date());
        product.setImage(data.getImage());
        product.setMrp(data.getMrp());
        product.setManufactured_date(data.getManufactured_date());

        return productService.addProduct(product);
    }

    @PostMapping("/products/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody ProductAddRequest data) {
        Product product = productService.getProductById(id);
        product.setName(data.getName());
        product.setPrice(data.getPrice());
        product.setDescription(data.getDescription());
        product.setCategory(data.getCategory());
        product.setCompany_name(data.getCompany_name());
        product.setExpiry_date(data.getExpiry_date());
        product.setImage(data.getImage());
        product.setMrp(data.getMrp());
        product.setManufactured_date(data.getManufactured_date());

        return productService.updateProduct(product);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }

}
