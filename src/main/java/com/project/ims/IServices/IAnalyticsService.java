package com.project.ims.IServices;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.ims.Responses.AnalyticsOverview;

@Service
public interface IAnalyticsService {

    public AnalyticsOverview getOverview();

    public Map<String, List<String>> getCurrentOrdersForDeliveryMan(String deliveryManId);

}
