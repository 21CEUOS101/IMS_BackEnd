package com.project.ims.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ims.IServices.IDistanceService;
import com.project.ims.Models.GlobalDistances;
import com.project.ims.Models.WareHouse;
import com.project.ims.Repo.GlobalDistancesRepo;
import com.project.ims.Repo.WareHouseRepo;

@Service
public class DistanceService implements IDistanceService {

    private static final Logger logger = LoggerFactory.getLogger(DistanceService.class);

    @Autowired
    private WareHouseRepo wareHouseRepo;

    @Autowired
    private GlobalDistancesRepo globalDistancesRepo;

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public int calculateDistance(String warehouseIdFrom, String warehouseIdTo) {

        GlobalDistances cached = globalDistancesRepo.findByFromAndTo(warehouseIdFrom, warehouseIdTo);

        if (cached != null) {
            return cached.getDistance().intValue();
        }

        WareHouse warehouseFrom = wareHouseRepo.findById(warehouseIdFrom).orElse(null);
        WareHouse warehouseTo = wareHouseRepo.findById(warehouseIdTo).orElse(null);

        if (warehouseFrom == null || warehouseTo == null) {
            throw new RuntimeException("WareHouse not found for distance calculation");
        }

        double[] coordsFrom = geocodePincode(warehouseFrom.getPincode());
        double[] coordsTo = geocodePincode(warehouseTo.getPincode());

        double distanceKm = haversineDistance(coordsFrom[0], coordsFrom[1], coordsTo[0], coordsTo[1]);

        GlobalDistances globalDistances = new GlobalDistances();
        globalDistances.setId(warehouseIdFrom + "_" + warehouseIdTo);
        globalDistances.setFrom(warehouseIdFrom);
        globalDistances.setTo(warehouseIdTo);
        globalDistances.setDistance(distanceKm);

        try {
            globalDistancesRepo.save(globalDistances);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return (int) Math.round(distanceKm);
    }

    private double[] geocodePincode(String pincode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "IMS-SupplyChainManagementSystem/1.0");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + pincode;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response.getBody());

            if (results.isEmpty()) {
                throw new RuntimeException("No coordinates found for pincode " + pincode);
            }

            JsonNode first = results.get(0);
            double lat = Double.parseDouble(first.get("lat").asText());
            double lon = Double.parseDouble(first.get("lon").asText());

            return new double[] { lat, lon };
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse geocoding response for pincode " + pincode, e);
        }
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
