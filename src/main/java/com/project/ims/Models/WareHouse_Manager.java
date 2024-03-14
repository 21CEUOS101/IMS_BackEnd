package com.project.ims.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "ware_house_managers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WareHouse_Manager {
    
    @Id
    private String id;

    private String warehouse_id;
}
