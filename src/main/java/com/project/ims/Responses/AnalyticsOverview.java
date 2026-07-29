package com.project.ims.Responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsOverview {

    private Double total_revenue;

    private Integer total_orders;

    private Double total_quantity;

    private Integer total_customers;

    private List<MonthRevenue> month_wise_revenue;

    private List<ProductRevenue> product_wise_revenue;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthRevenue {
        private String month;
        private String year;
        private Double revenue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductRevenue {
        private String product_name;
        private Double revenue;
    }
}
