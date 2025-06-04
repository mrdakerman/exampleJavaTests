package config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public class TestConfig {
    private static final String BASE_URL = "https://simplerestappfortest.onrender.com";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
        
        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setContentType("application/json")
                .build();
                
        RestAssured.requestSpecification = requestSpec;
    }
} 
