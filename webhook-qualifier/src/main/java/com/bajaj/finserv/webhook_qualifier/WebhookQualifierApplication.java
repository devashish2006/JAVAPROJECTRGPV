package com.bajajfinserv.webhookqualifier;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WebhookQualifierApplication implements CommandLineRunner {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public static void main(String[] args) {
        SpringApplication.run(WebhookQualifierApplication.class, args);
    }
    
    @Bean
	@Lazy  // Add this line
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting RGPV Bajaj Finserv Qualifier...");
        generateWebhookAndSolve();
    }
    
    private void generateWebhookAndSolve() {
        try {
            // Step 1: Generate webhook
            String webhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "Devashish Mishra");
            requestBody.put("regNo", "0105CS221062");
            requestBody.put("email", "mshubh612@gmail.com");
            
            System.out.println("Sending webhook generation request...");
            ResponseEntity<Map> response = restTemplate.postForEntity(webhookUrl, requestBody, Map.class);
            
            String returnedWebhook = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");
            
            System.out.println("Webhook received: " + returnedWebhook);
            
            // Step 2: Solve SQL problem and submit
            submitSolution(returnedWebhook, accessToken);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void submitSolution(String webhookUrl, String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", accessToken);  // No "Bearer " prefix
    
    // FINAL SQL SOLUTION for Question 2
    String sqlQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, COUNT(e2.EMP_ID) as YOUNGER_EMPLOYEES_COUNT FROM EMPLOYEE e1 JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e1.DOB GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME ORDER BY e1.EMP_ID DESC";
    
    Map<String, String> solutionBody = new HashMap<>();
    solutionBody.put("finalQuery", sqlQuery);  // Use "finalQuery" as per PDF
    
    HttpEntity<Map<String, String>> entity = new HttpEntity<>(solutionBody, headers);
    
    System.out.println("Submitting SQL solution...");
    String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    restTemplate.postForEntity(submitUrl, entity, String.class);
    System.out.println("Solution submitted successfully!");
}

}
