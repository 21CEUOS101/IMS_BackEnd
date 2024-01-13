package com.project.ims.Services;

// imports
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.project.ims.IServices.IWareHouseService;
import com.project.ims.Models.WareHouse;
import com.project.ims.Models.WareHouse_Manager;
import com.project.ims.Repo.WManagerRepo;
import com.project.ims.Repo.WareHouseRepo;

@Component
@Service
public class WareHouseService implements IWareHouseService {


    // necessary dependency Injections
    @Autowired
    private WareHouseRepo wareHouseRepo;

    @Autowired
    private WManagerRepo wManagerRepo;

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

        return wareHouseRepo.save(wareHouse);
    }

    @Override
    public WareHouse updateWareHouse(WareHouse wareHouse) {

        if (wareHouse == null)
        {
            throw new RuntimeException("WareHouse data shouldn't be null");
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
        wareHouseRepo.deleteById(id);
    }

    @Override
    public List<WareHouse> getAllWareHouse() {
        return wareHouseRepo.findAll();
    }
    
}
