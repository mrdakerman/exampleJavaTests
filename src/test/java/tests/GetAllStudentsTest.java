package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.model.Student;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.spec.Specifications.*;

@Execution(ExecutionMode.CONCURRENT)
class GetAllStudentsTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(GetAllStudentsTest.class);
    private static final Object restAssuredLock = new Object();

    @Test
    void shouldGetAllStudents() {
        logger.debug("Starting test: shouldGetAllStudents");
        synchronized (restAssuredLock) {
            Response response = given()
                .spec(requestSpec())
                .when()
                .get("/api/students")
                .then()
                .spec(responseSpec(200))
                .extract()
                .response();

            List<Student> students = response.jsonPath().getList(".", Student.class);
            logger.debug("Retrieved {} students from response", students.size());
            
            students.forEach(student -> 
                logger.debug("Student found: id={}, name={}, age={}, subjects={}", 
                           student.getId(), student.getName(), student.getAge(), student.getSubjects())
            );

            logger.debug("Verifying test students presence in the list");
            assertThat(students)
                .extracting("id")
                .contains(student1.get().getId(), student2.get().getId());

            assertThat(students)
                .extracting("name")
                .contains(student1.get().getName(), student2.get().getName());

            assertThat(students)
                .extracting("age")
                .contains(student1.get().getAge(), student2.get().getAge());

            logger.debug("Verifying test students subjects");
            assertThat(students)
                .filteredOn(s -> s.getId().equals(student1.get().getId()))
                .extracting("subjects")
                .contains(student1.get().getSubjects());

            assertThat(students)
                .filteredOn(s -> s.getId().equals(student2.get().getId()))
                .extracting("subjects")
                .contains(student2.get().getSubjects());
                
            logger.debug("Successfully verified all students data");
        }
    }
} 