package com.project.ims.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentSales {

    private String orderId;

    private String customerId;

    private Integer totalAmount;

    private String date;

    private Integer profit;
    
}
