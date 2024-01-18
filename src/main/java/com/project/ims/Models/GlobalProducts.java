package com.project.ims.Models;

// imports
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "globalProducts")
public class GlobalProducts {

    @Id
    private String id;

    private String quantity;

    private String name;

    private List<String> warehouses;

    private List<String> quantities;
    
}
