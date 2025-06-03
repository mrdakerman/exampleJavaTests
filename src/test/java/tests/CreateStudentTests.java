package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.model.Student;
import tests.utils.TestDataGenerator;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.spec.Specifications.*;

@Execution(ExecutionMode.CONCURRENT)
class CreateStudentTests extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(CreateStudentTests.class);
    private static final Object restAssuredLock = new Object();

    @Test
    void shouldCreateStudentWithValidData() {
        logger.debug("Starting test: shouldCreateStudentWithValidData");
        Student newStudent = TestDataGenerator.generateStudent();
        logger.debug("Generated new student: name={}, age={}, subjects={}", 
                    newStudent.getName(), newStudent.getAge(), newStudent.getSubjects());
        
        synchronized (restAssuredLock) {
            Response response = given()
                .spec(requestSpec())
                .body(newStudent)
                .when()
                .post("/api/students")
                .then()
                .spec(responseSpec(201))
                .extract()
                .response();

            Student createdStudent = response.as(Student.class);
            createdStudents.get().add(createdStudent);
            logger.debug("Created student with id: {}", createdStudent.getId());

            assertThat(createdStudent.getId()).isNotNull();
            assertThat(createdStudent)
                .extracting("name", "age")
                .containsExactly(newStudent.getName(), newStudent.getAge());
            assertThat(createdStudent.getSubjects())
                .containsExactlyElementsOf(newStudent.getSubjects());
            logger.debug("Successfully verified created student data");
        }
    }

    @Test
    void shouldCreateStudentWithMinimalData() {
        logger.debug("Starting test: shouldCreateStudentWithMinimalData");
        Student minimalStudent = new Student();
        minimalStudent.setName("Test Student");
        minimalStudent.setAge(20);
        minimalStudent.setSubjects(Collections.emptyList());
        logger.debug("Created minimal student: name={}, age={}, subjects=[]", 
                    minimalStudent.getName(), minimalStudent.getAge());

        synchronized (restAssuredLock) {
            Response response = given()
                .spec(requestSpec())
                .body(minimalStudent)
                .when()
                .post("/api/students")
                .then()
                .spec(responseSpec(201))
                .extract()
                .response();

            Student createdStudent = response.as(Student.class);
            createdStudents.get().add(createdStudent);
            logger.debug("Created student with id: {}", createdStudent.getId());

            assertThat(createdStudent.getId()).isNotNull();
            assertThat(createdStudent.getName()).isEqualTo(minimalStudent.getName());
            assertThat(createdStudent.getAge()).isEqualTo(minimalStudent.getAge());
            assertThat(createdStudent.getSubjects()).isEmpty();
            logger.debug("Successfully verified minimal student data");
        }
    }

    @Test
    void shouldCreateStudentWithNullFields() {
        logger.debug("Starting test: shouldCreateStudentWithNullFields");
        Student studentWithNulls = new Student();
        logger.debug("Created student with null fields");

        synchronized (restAssuredLock) {
            Response response = given()
                .spec(requestSpec())
                .body(studentWithNulls)
                .when()
                .post("/api/students")
                .then()
                .spec(responseSpec(201))
                .extract()
                .response();

            Student createdStudent = response.as(Student.class);
            createdStudents.get().add(createdStudent);
            logger.debug("Created student with id: {}", createdStudent.getId());

            assertThat(createdStudent.getId()).isNotNull();
            assertThat(createdStudent.getName()).isNull();
            assertThat(createdStudent.getAge()).isZero();
            assertThat(createdStudent.getSubjects()).isNull();
            logger.debug("Successfully verified null fields in created student");
        }
    }

    @Test
    void shouldReturn400WhenCreatingStudentWithId() {
        logger.debug("Starting test: shouldReturn400WhenCreatingStudentWithId");
        Student invalidStudent = TestDataGenerator.generateStudent();
        invalidStudent.setId(1L);
        logger.debug("Created invalid student with id={}", invalidStudent.getId());

        synchronized (restAssuredLock) {
            given()
                .spec(requestSpec())
                .body(invalidStudent)
                .when()
                .post("/api/students")
                .then()
                .spec(responseSpecWithoutContent(400));
            logger.debug("Successfully verified 400 response for student with ID");
        }
    }
} 