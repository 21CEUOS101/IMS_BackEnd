package com.project.ims.IServices;

import org.springframework.stereotype.Service;

import com.project.ims.Models.Supplier;

@Service
public interface ISupplierService {
    
    public Supplier addSupplier(Supplier supplier);

    public Supplier updateSupplier(Supplier supplier);

    public Supplier getSupplierById(String id);

    public void deleteSupplier(String id);
}
