package tests;

import config.TestConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.model.Student;
import tests.utils.TestDataGenerator;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static tests.spec.Specifications.*;

@Execution(ExecutionMode.CONCURRENT)
public class BaseTest extends TestConfig {
    protected static final Object restAssuredLock = new Object();
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    
    protected final ThreadLocal<List<Student>> createdStudents = ThreadLocal.withInitial(ArrayList::new);
    protected final ThreadLocal<Student> student1 = new ThreadLocal<>();
    protected final ThreadLocal<Student> student2 = new ThreadLocal<>();
    
    @BeforeEach
    void setUp() {
        logger.debug("Setting up test data...");
        student1.set(createStudent(TestDataGenerator.generateStudent()));
        student2.set(createStudent(TestDataGenerator.generateStudent()));
        logger.debug("Test data setup completed");
    }

    @AfterEach
    void tearDown() {
        logger.debug("Starting cleanup of test data...");
        for (Student student : createdStudents.get()) {
            if (student.getId() != null) {
                logger.debug("Deleting student with id: {}", student.getId());
                synchronized (restAssuredLock) {
                    Response response = given()
                        .spec(requestSpec())
                        .when()
                        .delete("/api/students/" + student.getId())
                        .then()
                        .extract()
                        .response();
                    
                    int statusCode = response.getStatusCode();
                    if (statusCode != 204 && statusCode != 404) {
                        logger.error("Failed to delete student with id: {}. Status code: {}", student.getId(), statusCode);
                        throw new AssertionError("Expected status code <204> or <404> but was <" + statusCode + ">");
                    }
                    logger.debug("Successfully deleted student with id: {}", student.getId());
                }
            }
        }
        createdStudents.remove();
        student1.remove();
        student2.remove();
        logger.debug("Test data cleanup completed");
    }

    protected Student createStudent(String name, int age, List<String> subjects) {
        return createStudent(new Student(name, age, subjects));
    }

    protected Student createStudent(Student student) {
        logger.debug("Creating new student: name={}, age={}, subjects={}", 
                    student.getName(), student.getAge(), student.getSubjects());
        
        synchronized (restAssuredLock) {
            Response response = given()
                .spec(requestSpec())
                .body(student)
                .when()
                .post("/api/students")
                .then()
                .spec(responseSpec(201))
                .extract()
                .response();

            Long studentId = response.jsonPath().getLong("id");
            student.setId(studentId);
            logger.debug("Student created successfully with id: {}", studentId);
            
            createdStudents.get().add(student);
            return student;
        }
    }
} 