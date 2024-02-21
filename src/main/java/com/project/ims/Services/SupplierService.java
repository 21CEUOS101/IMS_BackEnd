package com.project.ims.Services;

import java.util.ArrayList;
import java.util.HashMap;
// imports
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import com.project.ims.IServices.ISupplierService;

import com.project.ims.Models.Supplier;

import com.project.ims.Repo.SupplierRepo;


@Service
public class SupplierService implements ISupplierService {

    // necessary dependency Injections
    @Autowired
    private SupplierRepo supplierRepo;





    
   

    @Override
    public Supplier getSupplierById(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

        return supplierRepo.findById(id).orElse(null);
    }

    @Override
    public Supplier addSupplier(Supplier supplier) {

        if(supplier.getId() == null || supplier.getId().isEmpty())
        {
            throw new RuntimeException("Supplier ID cannot be empty");
        }
        else if(supplierRepo.existsById(supplier.getId()))
        {
            throw new RuntimeException("Supplier ID already exists");
        }
        else if (supplier.getId().charAt(0) != 's')
        {
            throw new RuntimeException("Supplier ID must start with 's'");
        }

        return supplierRepo.save(supplier);
    }

    @Override
    public Supplier updateSupplier(Supplier supplier) {

        if(supplier == null)
        {
            throw new RuntimeException("Supplier data shouldn't be null");
        }

        return supplierRepo.save(supplier);
    }

    @Override
    public void deleteSupplier(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }
        else if (!supplierRepo.existsById(id))
        {
            throw new RuntimeException("Supplier with id " + id + " does not exist");
        }
        
        supplierRepo.deleteById(id);
    }

    @Override
    public List<Supplier> getAllSupplier() {
        return supplierRepo.findAll();
    }
  
}
