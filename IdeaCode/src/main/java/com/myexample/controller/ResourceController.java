package com.myexample.controller;

import com.myexample.service.PythonAPIService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final PythonAPIService pythonAPIService;

    @Autowired
    public ResourceController(PythonAPIService pythonAPIService) {
        this.pythonAPIService = pythonAPIService;
    }

    @GetMapping("/predict")
    public ResponseEntity<?> predict(@RequestParam(defaultValue = "1") int hours) {
        try {
            if (!pythonAPIService.isPythonServiceHealthy()) {
                return ResponseEntity.status(503)
                        .body("Python预测服务不可用");
            }
            return ResponseEntity.ok(pythonAPIService.getResourcePrediction(hours));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("预测失败: " + e.getMessage());
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUsage() {
        try {
            if (!pythonAPIService.isPythonServiceHealthy()) {
                return ResponseEntity.status(503)
                        .body("Python预测服务不可用");
            }
            return ResponseEntity.ok(pythonAPIService.getCurrentUsage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("获取当前使用率失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return pythonAPIService.isPythonServiceHealthy()
                ? ResponseEntity.ok("OK")
                : ResponseEntity.status(503).body("Python服务连接失败");
    }
}