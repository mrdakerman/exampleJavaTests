package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.model.Student;
import tests.utils.TestDataGenerator;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.spec.Specifications.*;

@Execution(ExecutionMode.CONCURRENT)
class DeleteStudentTests extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DeleteStudentTests.class);
    private static final Object restAssuredLock = new Object();

    @Test
    void shouldDeleteExistingStudent() {
        logger.debug("Starting test: shouldDeleteExistingStudent");
        Student studentToDelete = createStudent(TestDataGenerator.generateStudent());
        logger.debug("Created test student with id: {}", studentToDelete.getId());
        
        synchronized (restAssuredLock) {
            logger.debug("Attempting to delete student with id: {}", studentToDelete.getId());
            given()
                .spec(requestSpec())
                .when()
                .delete("/api/students/" + studentToDelete.getId())
                .then()
                .spec(responseSpecWithoutContent(204));

            logger.debug("Verifying student deletion with id: {}", studentToDelete.getId());
            given()
                .spec(requestSpec())
                .when()
                .get("/api/students/" + studentToDelete.getId())
                .then()
                .spec(responseSpecWithoutContent(404));
            logger.debug("Successfully verified student deletion");
        }
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentStudent() {
        logger.debug("Starting test: shouldReturn404WhenDeletingNonExistentStudent");
        synchronized (restAssuredLock) {
            logger.debug("Attempting to delete non-existent student with id: 999999");
            given()
                .spec(requestSpec())
                .when()
                .delete("/api/students/999999")
                .then()
                .spec(responseSpecWithoutContent(404));
            logger.debug("Successfully verified 404 response for non-existent student");
        }
    }
} 