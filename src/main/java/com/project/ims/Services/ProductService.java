package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.ims.IServices.IProductService;
import com.project.ims.Models.Product;
import com.project.ims.Repo.AdminRepo;
import com.project.ims.Repo.CustomerRepo;
import com.project.ims.Repo.DeliveryManRepo;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.SupplierRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
@Service
public class ProductService implements IProductService {
    
    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private DeliveryManRepo deliveryManRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private SupplierRepo supplierRepo;

    @Autowired
    private WareHouseRepo wareHouseRepo;

    @Autowired
    private WManagerRepo wManagerRepo;

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

        return productRepo.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepo.existsById(id))
        {
            throw new RuntimeException("Product with id " + id + " does not exist");
        }

        productRepo.deleteById(id);
    }

    public List<Product> getAllProduct() {
        return productRepo.findAll();
    }
    
}
