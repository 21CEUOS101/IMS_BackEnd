package com.project.ims.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.ims.Responses.AnalyticsOverview;
import com.project.ims.Services.AnalyticsService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"https://ashish2901-ims.vercel.app/","http://localhost:3000","http://localhost:3001","https://ims-frontend-eight.vercel.app/"}, allowedHeaders = "*", allowCredentials = "true")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // dashboard revenue/order/quantity/customer totals + month & product wise revenue
    // formerly served by the standalone analytics-api Flask service
    @GetMapping("/admin/analytics/overview")
    public AnalyticsOverview getOverview() {
        try {
            return analyticsService.getOverview();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // ids of every shipped order (across all 5 order types) currently assigned to a delivery man
    // formerly served by the standalone analytics-api Flask service
    @GetMapping("/deliveryman/{id}/current-order")
    public Map<String, List<String>> getCurrentOrderForDeliveryMan(@PathVariable("id") String id) {
        try {
            return analyticsService.getCurrentOrdersForDeliveryMan(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
