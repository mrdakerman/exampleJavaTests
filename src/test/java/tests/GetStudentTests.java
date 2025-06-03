package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.model.Student;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.spec.Specifications.*;

@Execution(ExecutionMode.CONCURRENT)
class GetStudentTests extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(GetStudentTests.class);
    private static final Object restAssuredLock = new Object();

    @Test
    void shouldGetStudentById() {
        logger.debug("Starting test: shouldGetStudentById for student with id: {}", student1.get().getId());
        synchronized (restAssuredLock) {
            Response response = given()
                .spec(requestSpec())
                .when()
                .get("/api/students/" + student1.get().getId())
                .then()
                .spec(responseSpec(200))
                .extract()
                .response();

            Student retrievedStudent = response.as(Student.class);
            logger.debug("Retrieved student: id={}, name={}, age={}, subjects={}", 
                        retrievedStudent.getId(), retrievedStudent.getName(), 
                        retrievedStudent.getAge(), retrievedStudent.getSubjects());
            
            assertThat(retrievedStudent)
                .extracting("id", "name", "age")
                .containsExactly(student1.get().getId(), student1.get().getName(), student1.get().getAge());
                
            assertThat(retrievedStudent.getSubjects())
                .containsExactlyElementsOf(student1.get().getSubjects());
                
            logger.debug("Successfully verified student data matches");
        }
    }

    @Test
    void shouldReturn404WhenStudentNotFound() {
        logger.debug("Starting test: shouldReturn404WhenStudentNotFound");
        synchronized (restAssuredLock) {
            logger.debug("Attempting to get non-existent student with id: 999999");
            given()
                .spec(requestSpec())
                .when()
                .get("/api/students/999999")
                .then()
                .spec(responseSpecWithoutContent(404));
            logger.debug("Successfully verified 404 response for non-existent student");
        }
    }
} 