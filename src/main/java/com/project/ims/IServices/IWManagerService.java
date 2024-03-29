package com.project.ims.IServices;
import java.util.List;
import org.springframework.stereotype.Service;
import com.project.ims.Models.WareHouse_Manager;

@Service
public interface IWManagerService {

    public List<WareHouse_Manager> getAllWManager();
    public WareHouse_Manager addWManager(WareHouse_Manager wManager);
    public WareHouse_Manager updateWManager(WareHouse_Manager wManager);
    public WareHouse_Manager getWManagerById(String id);
    public void deleteWManager(String id);
}
