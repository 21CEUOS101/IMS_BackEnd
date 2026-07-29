package com.project.ims.Services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.ims.IServices.IAnalyticsService;
import com.project.ims.Models.Order;
import com.project.ims.Models.Product;
import com.project.ims.Repo.OrderRepo;
import com.project.ims.Repo.ProductRepo;
import com.project.ims.Repo.RSORepo;
import com.project.ims.Repo.ReturnOrderRepo;
import com.project.ims.Repo.SupplyOrderRepo;
import com.project.ims.Repo.W2WOrderRepo;
import com.project.ims.Responses.AnalyticsOverview;
import com.project.ims.Responses.AnalyticsOverview.MonthRevenue;
import com.project.ims.Responses.AnalyticsOverview.ProductRevenue;

@Service
public class AnalyticsService implements IAnalyticsService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ReturnOrderRepo returnOrderRepo;

    @Autowired
    private RSORepo rsoRepo;

    @Autowired
    private SupplyOrderRepo supplyOrderRepo;

    @Autowired
    private W2WOrderRepo w2wOrderRepo;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AnalyticsOverview getOverview() {

        List<Order> orders = orderRepo.findAll();
        List<Product> products = productRepo.findAll();

        Map<String, Double> revenuePerUnitByProductId = products.stream()
                .collect(Collectors.toMap(Product::getId,
                        p -> (p.getProfit() / 100.0) * p.getWhole_sale_price()));

        Map<String, String> productNameById = products.stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));

        double totalQuantity = 0;
        for (Order order : orders) {
            totalQuantity += parseDoubleOrZero(order.getQuantity());
        }

        int totalCustomers = (int) orders.stream()
                .map(Order::getCustomerId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();

        List<Order> deliveredOrders = orders.stream()
                .filter(o -> "delivered".equals(o.getStatus()))
                .collect(Collectors.toList());

        double totalRevenue = 0;
        for (Order order : deliveredOrders) {
            totalRevenue += orderRevenue(order, revenuePerUnitByProductId);
        }

        Map<String, Double> revenueByMonthYear = new LinkedHashMap<>();
        Map<String, Double> revenueByProductName = new LinkedHashMap<>();

        for (Order order : deliveredOrders) {
            double revenue = orderRevenue(order, revenuePerUnitByProductId);

            LocalDateTime deliveredAt = parseDateTimeOrNull(order.getDelivered_date_time());
            if (deliveredAt != null) {
                String monthName = deliveredAt.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                String key = monthName + "|" + deliveredAt.getYear();
                revenueByMonthYear.merge(key, revenue, Double::sum);
            }

            String productName = productNameById.get(order.getProduct_id());
            if (productName != null) {
                revenueByProductName.merge(productName, revenue, Double::sum);
            }
        }

        List<MonthRevenue> monthWiseRevenue = revenueByMonthYear.entrySet().stream()
                .map(e -> {
                    String[] parts = e.getKey().split("\\|");
                    return new MonthRevenue(parts[0], parts[1], e.getValue());
                })
                .collect(Collectors.toList());

        List<ProductRevenue> productWiseRevenue = revenueByProductName.entrySet().stream()
                .map(e -> new ProductRevenue(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingDouble(ProductRevenue::getRevenue).reversed())
                .collect(Collectors.toList());

        return new AnalyticsOverview(totalRevenue, orders.size(), totalQuantity, totalCustomers,
                monthWiseRevenue, productWiseRevenue);
    }

    @Override
    public Map<String, List<String>> getCurrentOrdersForDeliveryMan(String deliveryManId) {

        Map<String, List<String>> result = new LinkedHashMap<>();

        result.put("order", shippedIdsFor(orderRepo.findAll(), deliveryManId, Order::getStatus, Order::getDelivery_man_id, Order::getId));
        result.put("return-order", shippedIdsFor(returnOrderRepo.findAll(), deliveryManId,
                com.project.ims.Models.ReturnOrder::getStatus, com.project.ims.Models.ReturnOrder::getDelivery_man_id,
                com.project.ims.Models.ReturnOrder::getId));
        result.put("return-supply-order", shippedIdsFor(rsoRepo.findAll(), deliveryManId,
                com.project.ims.Models.ReturnSupplyOrder::getStatus, com.project.ims.Models.ReturnSupplyOrder::getDelivery_man_id,
                com.project.ims.Models.ReturnSupplyOrder::getId));
        result.put("supply-order", shippedIdsFor(supplyOrderRepo.findAll(), deliveryManId,
                com.project.ims.Models.SupplyOrder::getStatus, com.project.ims.Models.SupplyOrder::getDelivery_man_id,
                com.project.ims.Models.SupplyOrder::getId));
        result.put("w2worder", shippedIdsFor(w2wOrderRepo.findAll(), deliveryManId,
                com.project.ims.Models.W2WOrder::getStatus, com.project.ims.Models.W2WOrder::getDelivery_man_id,
                com.project.ims.Models.W2WOrder::getId));

        return result;
    }

    private <T> List<String> shippedIdsFor(List<T> items, String deliveryManId,
            java.util.function.Function<T, String> statusGetter,
            java.util.function.Function<T, String> deliveryManIdGetter,
            java.util.function.Function<T, String> idGetter) {

        List<String> ids = new ArrayList<>();
        for (T item : items) {
            if ("shipped".equals(statusGetter.apply(item)) && deliveryManId.equals(deliveryManIdGetter.apply(item))) {
                ids.add(idGetter.apply(item));
            }
        }
        return ids;
    }

    private double orderRevenue(Order order, Map<String, Double> revenuePerUnitByProductId) {
        Double revenuePerUnit = revenuePerUnitByProductId.get(order.getProduct_id());
        if (revenuePerUnit == null) {
            return 0;
        }
        return revenuePerUnit * parseDoubleOrZero(order.getQuantity());
    }

    private double parseDoubleOrZero(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private LocalDateTime parseDateTimeOrNull(String value) {
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}
