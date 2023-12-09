package com.project.ims.IServices;

import org.springframework.stereotype.Service;

import com.project.ims.Models.WareHouse;

@Service
public interface IWareHouseService {
    public WareHouse addWareHouse(WareHouse wareHouse);

    public WareHouse updateWareHouse(WareHouse wareHouse);

    public WareHouse getWareHouseById(String id);

    public void deleteWareHouse(String id);
    
}
