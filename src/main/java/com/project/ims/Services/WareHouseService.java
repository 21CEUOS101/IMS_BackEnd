package com.project.ims.Services;

// imports
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IWareHouseService;
import com.project.ims.Models.GlobalProducts;
import com.project.ims.Models.WareHouse;
import com.project.ims.Models.WareHouse_Manager;
import com.project.ims.Repo.GlobalProductsRepo;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Service
public class WareHouseService implements IWareHouseService {


    // necessary dependency Injections
    @Autowired
    private WareHouseRepo wareHouseRepo;

    @Autowired
    private WManagerRepo wManagerRepo;

    @Autowired
    private GPService gpService;

    // Services


    @Override
    public WareHouse getWareHouseById(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }

        return wareHouseRepo.findById(id).orElse(null);
    }

    @Override
    public WareHouse addWareHouse(WareHouse wareHouse) {

        if (wareHouse == null)
        {
            throw new RuntimeException("WareHouse data shouldn't be null");
        }
        else if (wareHouse.getId() == null || wareHouse.getId().isEmpty())
        {
            throw new RuntimeException("WareHouse ID cannot be empty");
        }
        else if (wareHouseRepo.existsById(wareHouse.getId()))
        {
            throw new RuntimeException("WareHouse ID already exists");
        }
        else if (wareHouse.getId().charAt(0) != 'w')
        {
            throw new RuntimeException("WareHouse ID must start with 'w'");
        }

        // check if manager id exists

        if(wareHouse.getManager_id() == null)
        {
            throw new RuntimeException("WareHouse Manager ID cannot be empty");
        }
        else if (wareHouse.getManager_id().charAt(0) != 'm')
        {
            throw new RuntimeException("WareHouse Manager ID must start with 'm'");
        }

        WareHouse_Manager wManager = wManagerRepo.findById(wareHouse.getManager_id()).orElse(null);

        if (wManager == null)
        {
            throw new RuntimeException("WareHouse Manager ID does not exist");
        }

        wManager.setWarehouse_id(wareHouse.getId());

        try{
            wManagerRepo.save(wManager);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }


        String id = wareHouse.getId();

        // add to global products
        for(int i=0;i<wareHouse.getProduct_ids().size();i++)
        {
            String productId = wareHouse.getProduct_ids().get(i);
            Integer quantity = Integer.parseInt(wareHouse.getQuantities().get(i));

            GlobalProducts globalProducts = gpService.getById(productId);

            if(globalProducts == null)
            {
                throw new RuntimeException("Product with id " + productId + " does not exist");
            }

            try{
                gpService.addByWarehouse(productId, id, quantity);
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        return wareHouseRepo.save(wareHouse);
    }

    @Override
    public WareHouse updateWareHouse(WareHouse wareHouse) {

        if (wareHouse == null)
        {
            throw new RuntimeException("WareHouse data shouldn't be null");
        }

        String id = wareHouse.getId();

        // update global products
        for(int i=0;i<wareHouse.getProduct_ids().size();i++)
        {
            String productId = wareHouse.getProduct_ids().get(i);
            Integer quantity = Integer.parseInt(wareHouse.getQuantities().get(i));

            GlobalProducts globalProducts = gpService.getById(productId);

            if(globalProducts == null)
            {
                throw new RuntimeException("Product with id " + productId + " does not exist");
            }

            try{
                gpService.updateByWarehouse(productId, id, quantity);
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        return wareHouseRepo.save(wareHouse);
    }

    @Override
    public void deleteWareHouse(String id) {

        if (id == null)
        {
            throw new RuntimeException("Id shouldn't be null");
        }
        else if (!wareHouseRepo.existsById(id))
        {
            throw new RuntimeException("WareHouse ID does not exist");
        }

        WareHouse wareHouse = wareHouseRepo.findById(id).orElse(null);

        // update global products

        for(int i=0;i<wareHouse.getProduct_ids().size();i++)
        {
            String productId = wareHouse.getProduct_ids().get(i);

            GlobalProducts globalProducts = gpService.getById(productId);

            if(globalProducts == null)
            {
                throw new RuntimeException("Product with id " + productId + " does not exist");
            }

            try{
                gpService.deleteByWarehouse(productId, id);
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        wareHouseRepo.deleteById(id);
    }

    @Override
    public List<WareHouse> getAllWareHouse() {
        return wareHouseRepo.findAll();
    }
    
}
