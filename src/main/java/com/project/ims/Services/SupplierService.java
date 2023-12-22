package com.project.ims.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.ims.IServices.ISupplierService;
import com.project.ims.Models.Supplier;
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
public class SupplierService implements ISupplierService {
    
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
    public Supplier getSupplierById(String id) {
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
        return supplierRepo.save(supplier);
    }

    @Override
    public void deleteSupplier(String id) {
        
        if (!supplierRepo.existsById(id))
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
