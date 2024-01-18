package com.project.ims.Models;

// imports
import java.util.HashMap;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "globalWarehouses")
public class GlobalWarehouses {

    @Id
    private String id;

    private HashMap<String , Integer> distances;
    
}
