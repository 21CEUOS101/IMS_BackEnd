package com.project.ims.Models;

// imports
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "delivery_man")
public class DeliveryMan {

    @Id
    private String id;
    
    private String warehouseId;

    private String status;

}
