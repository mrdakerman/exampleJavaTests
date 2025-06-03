package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.model.Student;
import tests.utils.TestDataGenerator;

import java.util.Arrays;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.spec.Specifications.*;

@Execution(ExecutionMode.CONCURRENT)
class UpdateStudentTests extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(UpdateStudentTests.class);
    private static final Object restAssuredLock = new Object();

    @Test
    void shouldUpdateStudentWithValidData() {
        logger.debug("Starting test: shouldUpdateStudentWithValidData for student id: {}", student1.get().getId());
        Student updatedStudent = new Student(
            TestDataGenerator.generateName(),
            TestDataGenerator.generateAge(),
            Arrays.asList("Updated Subject 1", "Updated Subject 2")
        );
        updatedStudent.setId(student1.get().getId());
        logger.debug("Created update data: name={}, age={}, subjects={}", 
                    updatedStudent.getName(), updatedStudent.getAge(), updatedStudent.getSubjects());

        synchronized (restAssuredLock) {
            Response response = given()
                .spec(requestSpec())
                .body(updatedStudent)
                .when()
                .put("/api/students/" + student1.get().getId())
                .then()
                .spec(responseSpec(200))
                .extract()
                .response();

            Student returnedStudent = response.as(Student.class);
            logger.debug("Received updated student response");

            assertThat(returnedStudent)
                .extracting("id", "name", "age")
                .containsExactly(student1.get().getId(), updatedStudent.getName(), updatedStudent.getAge());
                
            assertThat(returnedStudent.getSubjects())
                .containsExactlyElementsOf(updatedStudent.getSubjects());
            logger.debug("Verified update response data");

            logger.debug("Verifying data persistence with GET request");
            Response getResponse = given()
                .spec(requestSpec())
                .when()
                .get("/api/students/" + student1.get().getId())
                .then()
                .spec(responseSpec(200))
                .extract()
                .response();

            Student retrievedStudent = getResponse.as(Student.class);
            assertThat(retrievedStudent)
                .extracting("name", "age")
                .containsExactly(updatedStudent.getName(), updatedStudent.getAge());
            assertThat(retrievedStudent.getSubjects())
                .containsExactlyElementsOf(updatedStudent.getSubjects());
            logger.debug("Successfully verified data persistence");
        }
    }

    @Test
    void shouldUpdateStudentWithMinimalData() {
        logger.debug("Starting test: shouldUpdateStudentWithMinimalData for student id: {}", student1.get().getId());
        Student minimalUpdate = new Student();
        minimalUpdate.setId(student1.get().getId());
        minimalUpdate.setName("Updated Name");
        minimalUpdate.setAge(25);
        minimalUpdate.setSubjects(Collections.emptyList());
        logger.debug("Created minimal update data: name={}, age={}, subjects=[]", 
                    minimalUpdate.getName(), minimalUpdate.getAge());

        synchronized (restAssuredLock) {
            Response response = given()
                .spec(requestSpec())
                .body(minimalUpdate)
                .when()
                .put("/api/students/" + student1.get().getId())
                .then()
                .spec(responseSpec(200))
                .extract()
                .response();

            Student updatedStudent = response.as(Student.class);
            logger.debug("Received updated student response");

            assertThat(updatedStudent.getName()).isEqualTo(minimalUpdate.getName());
            assertThat(updatedStudent.getAge()).isEqualTo(minimalUpdate.getAge());
            assertThat(updatedStudent.getSubjects()).isEmpty();
            logger.debug("Successfully verified minimal update data");
        }
    }

    @Test
    void shouldUpdateStudentWithNullFields() {
        logger.debug("Starting test: shouldUpdateStudentWithNullFields for student id: {}", student1.get().getId());
        Student nullFieldsUpdate = new Student();
        nullFieldsUpdate.setId(student1.get().getId());
        logger.debug("Created update with null fields");

        synchronized (restAssuredLock) {
            Response response = given()
                .spec(requestSpec())
                .body(nullFieldsUpdate)
                .when()
                .put("/api/students/" + student1.get().getId())
                .then()
                .spec(responseSpec(200))
                .extract()
                .response();

            Student updatedStudent = response.as(Student.class);
            logger.debug("Received updated student response");

            assertThat(updatedStudent.getId()).isEqualTo(student1.get().getId());
            assertThat(updatedStudent.getName()).isNull();
            assertThat(updatedStudent.getAge()).isZero();
            assertThat(updatedStudent.getSubjects()).isNull();
            logger.debug("Successfully verified null fields update");
        }
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentStudent() {
        logger.debug("Starting test: shouldReturn404WhenUpdatingNonExistentStudent");
        Student updatedStudent = TestDataGenerator.generateStudent();
        updatedStudent.setId(999999L);
        logger.debug("Created update data for non-existent student id: {}", updatedStudent.getId());

        synchronized (restAssuredLock) {
            given()
                .spec(requestSpec())
                .body(updatedStudent)
                .when()
                .put("/api/students/999999")
                .then()
                .spec(responseSpecWithoutContent(404));
            logger.debug("Successfully verified 404 response for non-existent student");
        }
    }
} 