package com.bajajfinserv.health.bajaj_finserv_solution;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootApplication
public class BajajFinservSolutionApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BajajFinservSolutionApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("✅ Application started...");

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Step 1: Generate Webhook
            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "Surasani Vedasri Varshini");              
            requestBody.put("regNo", "22BEC7048");              
            requestBody.put("email", "varshini.22bec7048@vitapstudent.ac.in");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    generateUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            String webhookUrl = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");

            System.out.println("✅ Webhook URL: " + webhookUrl);
            System.out.println("✅ Access Token: " + accessToken);

            // Step 2: SQL Query 
            String finalQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, "
                    + "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT "
                    + "FROM EMPLOYEE e1 "
                    + "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID "
                    + "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e1.DOB "
                    + "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME "
                    + "ORDER BY e1.EMP_ID DESC;";

            Map<String, String> solutionBody = new HashMap<>();
            solutionBody.put("finalQuery", finalQuery);

            HttpHeaders solutionHeaders = new HttpHeaders();
            solutionHeaders.setContentType(MediaType.APPLICATION_JSON);
            solutionHeaders.set("Authorization", accessToken);

            HttpEntity<Map<String, String>> solutionEntity = new HttpEntity<>(solutionBody, solutionHeaders);

            ResponseEntity<String> solutionResponse = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    solutionEntity,
                    String.class
            );

            System.out.println("✅ Solution Response: " + solutionResponse.getBody());

        } catch (Exception e) {
            System.err.println("❌ Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}