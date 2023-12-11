package com.project.ims.IServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.ims.Models.Supplier;

@Service
public interface ISupplierService {

    public List<Supplier> getAllSupplier();
    
    public Supplier addSupplier(Supplier supplier);

    public Supplier updateSupplier(Supplier supplier);

    public Supplier getSupplierById(String id);

    public void deleteSupplier(String id);
}
