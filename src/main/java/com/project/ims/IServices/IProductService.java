package com.project.ims.IServices;
import java.util.List;
import org.springframework.stereotype.Service;
import com.project.ims.Models.Product;

@Service
public interface IProductService {

        public List<Product> getAllProduct();
        public Product getProductById(String id);
        public Product addProduct(Product product);
        public Product updateProduct(Product product);
        public void deleteProduct(String id);
        public List<Product> getProductBySupplireId(String id);
}
