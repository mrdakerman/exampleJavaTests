package tests.spec;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Specifications {
    
    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    }

    public static ResponseSpecification responseSpec(int expectedStatusCode) {
        return new ResponseSpecBuilder()
            .expectStatusCode(expectedStatusCode)
            .expectContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    }

    public static ResponseSpecification responseSpecWithoutContent(int expectedStatusCode) {
        return new ResponseSpecBuilder()
            .expectStatusCode(expectedStatusCode)
            .log(LogDetail.ALL)
            .build();
    }
} 