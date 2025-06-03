# Example Java API Tests

This project contains example API tests for a Spring Boot REST application using:
- JUnit 5 for test framework
- REST-assured for API testing
- AssertJ for assertions
- SLF4J with Logback for logging
- Gradle for build automation

## Project Structure

```
src/test/java/
├── config/
│   └── TestConfig.java         # Base configuration for tests
├── tests/
│   ├── model/
│   │   └── Student.java        # Test data model
│   ├── spec/
│   │   └── Specifications.java # REST-assured specifications
│   ├── utils/
│   │   └── TestDataGenerator.java # Test data generation utilities
│   ├── BaseTest.java          # Base test class with common functionality
│   ├── CreateStudentTests.java # Tests for student creation
│   ├── DeleteStudentTests.java # Tests for student deletion
│   ├── GetAllStudentsTest.java # Tests for getting all students
│   ├── GetStudentTests.java    # Tests for getting single student
│   └── UpdateStudentTests.java # Tests for student updates
└── resources/
    └── logback-test.xml       # Logging configuration
```

## Features

- Parallel test execution
- Thread-safe test design
- Comprehensive logging
- Automatic test data cleanup
- Reusable test specifications
- Random test data generation

## Running Tests

To run the tests, execute:

```bash
./gradlew test
```

Test results will be available in `build/reports/tests/test/index.html`

## Logging

Test execution logs are written to:
- Console output
- `build/test-output/test.log` file

Log levels can be configured in `src/test/resources/logback-test.xml` 