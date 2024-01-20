package com.project.ims.Services;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.ims.Models.GlobalProducts;
import com.project.ims.Repo.GlobalProductsRepo;

@Service
public class GPService {

    @Autowired
    private GlobalProductsRepo globalProductsRepo;

    public GPService(GlobalProductsRepo globalProductsRepo) {
        this.globalProductsRepo = globalProductsRepo;
    }

    // get all products
    public List<GlobalProducts> getAll()
    {
        return globalProductsRepo.findAll();
    }

    // get product by id
    public GlobalProducts getById(String productId)
    {
        if(productId == null || productId.isEmpty())
            throw new RuntimeException("Product ID cannot be empty");

        GlobalProducts globalProducts = globalProductsRepo.findById(productId).orElse(null);

        if (globalProducts == null) {
            throw new RuntimeException("Product with id " + productId + " does not exist");
        }

        return globalProducts;
    }

    // add product
    public GlobalProducts add(String productId , String name , List<String> warehouses , List<String> quantities)
    {
        GlobalProducts globalProducts = new GlobalProducts(productId, "0", name, warehouses , quantities);

        int quantity = 0;

        for (String q : quantities) {
            quantity += Integer.parseInt(q);
        }

        globalProducts.setQuantity(String.valueOf(quantity));

        return globalProductsRepo.save(globalProducts);
    }
    
    // update product
    public GlobalProducts update(String productId , String name , List<String> warehouses , List<String> quantities)
    {
        GlobalProducts globalProducts = globalProductsRepo.findById(productId).orElse(null);

        if (globalProducts == null) {
            throw new RuntimeException("Product with id " + productId + " does not exist");
        }

        globalProducts.setName(name);
        globalProducts.setWarehouses(warehouses);
        globalProducts.setQuantities(quantities);

        int quantity = 0;

        for (String q : quantities) {
            quantity += Integer.parseInt(q);
        }

        globalProducts.setQuantity(String.valueOf(quantity));

        return globalProductsRepo.save(globalProducts);
    }

    // add by warehouse
    public GlobalProducts addByWarehouse(String productId , String warehouseId , Integer quantity)
    {
        GlobalProducts globalProducts = globalProductsRepo.findById(productId).orElse(null);

        if (globalProducts == null) {
            throw new RuntimeException("Product with id " + productId + " does not exist");
        }

        List<String> warehouses = globalProducts.getWarehouses();
        List<String> quantities = globalProducts.getQuantities();

        if (warehouses.contains(warehouseId)) {
            throw new RuntimeException("Warehouse with id " + warehouseId + " already exists");
        }

        warehouses.add(warehouseId);
        quantities.add(String.valueOf(quantity));

        globalProducts.setWarehouses(warehouses);

        int totalQuantity = 0;

        for(String q : quantities)
        {
            totalQuantity += Integer.parseInt(q);
        }

        globalProducts.setQuantity(String.valueOf(totalQuantity));

        return globalProductsRepo.save(globalProducts);
    }

    // update by warehouse
    public GlobalProducts updateByWarehouse(String productId , String warehouseId , Integer quantity)
    {
        GlobalProducts globalProducts = globalProductsRepo.findById(productId).orElse(null);

        if (globalProducts == null) {
            throw new RuntimeException("Product with id " + productId + " does not exist");
        }

        List<String> warehouses = globalProducts.getWarehouses();
        List<String> quantities = globalProducts.getQuantities();

        if (warehouses.contains(warehouseId)) {
            int index = warehouses.indexOf(warehouseId);
            quantities.set(index, String.valueOf(quantity));
        } else {
            throw new RuntimeException("Warehouse with id " + warehouseId + " does not exist");
        }

        globalProducts.setWarehouses(warehouses);

        int totalQuantity = 0;

        for(String q : quantities)
        {
            totalQuantity += Integer.parseInt(q);
        }

        globalProducts.setQuantity(String.valueOf(totalQuantity));

        return globalProductsRepo.save(globalProducts);
    }
    
    // delete by warehouse
    public GlobalProducts deleteByWarehouse(String productId , String warehouseId)
    {
        GlobalProducts globalProducts = globalProductsRepo.findById(productId).orElse(null);

        if (globalProducts == null) {
            throw new RuntimeException("Product with id " + productId + " does not exist");
        }

        List<String> warehouses = globalProducts.getWarehouses();
        List<String> quantities = globalProducts.getQuantities();

        if (warehouses.contains(warehouseId)) {
            int index = warehouses.indexOf(warehouseId);
            warehouses.remove(warehouseId);
            quantities.remove(index);
        } else {
            throw new RuntimeException("Warehouse with id " + warehouseId + " does not exist");
        }

        globalProducts.setWarehouses(warehouses);

        int totalQuantity = 0;

        for(String q : quantities)
        {
            totalQuantity += Integer.parseInt(q);
        }

        globalProducts.setQuantity(String.valueOf(totalQuantity));

        return globalProductsRepo.save(globalProducts);
    }
    
    // delete product
    public void delete(String productId)
    {
        GlobalProducts globalProducts = globalProductsRepo.findById(productId).orElse(null);

        if (globalProducts == null) {
            throw new RuntimeException("Product with id " + productId + " does not exist");
        }

        globalProductsRepo.deleteById(productId);
    }
    
}
