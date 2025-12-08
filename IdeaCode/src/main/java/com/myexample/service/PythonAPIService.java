package com.myexample.service;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PythonAPIService {
    private static final Logger logger = LoggerFactory.getLogger(PythonAPIService.class);

    @Value("${python.api.url}")
    private String pythonApiUrl;

    @Value("${python.api.predict-endpoint}")
    private String predictEndpoint;

    @Value("${python.api.current-endpoint}")
    private String currentEndpoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "resourceCache", key = "#hours")
    public JsonNode getResourcePrediction(int hours) throws Exception {
        String url = String.format("%s%s?hours=%d", pythonApiUrl, predictEndpoint, hours);
        logger.info("Fetching prediction from: {}", url);
        return executeGetRequest(url);
    }

    public JsonNode getCurrentUsage() throws Exception {
        String url = pythonApiUrl + currentEndpoint;
        logger.info("Fetching current usage from: {}", url);
        return executeGetRequest(url);
    }

    private JsonNode executeGetRequest(String url) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            return client.execute(request, response -> {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("API request failed with status: " +
                            response.getStatusLine().getStatusCode());
                }
                String json = EntityUtils.toString(response.getEntity());
                return objectMapper.readTree(json);
            });
        }
    }

    public boolean isPythonServiceHealthy() {
        try {
            executeGetRequest(pythonApiUrl + "/health");
            return true;
        } catch (Exception e) {
            logger.warn("Python service health check failed", e);
            return false;
        }
    }
}