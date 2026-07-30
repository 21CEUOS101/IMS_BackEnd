package com.project.ims.Responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsOverview {

    @JsonProperty("total_revenue")
    private Double totalRevenue;

    @JsonProperty("total_orders")
    private Integer totalOrders;

    @JsonProperty("total_quantity")
    private Double totalQuantity;

    @JsonProperty("total_customers")
    private Integer totalCustomers;

    @JsonProperty("month_wise_revenue")
    private List<MonthRevenue> monthWiseRevenue;

    @JsonProperty("product_wise_revenue")
    private List<ProductRevenue> productWiseRevenue;

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
        @JsonProperty("product_name")
        private String productName;
        private Double revenue;
    }
}
