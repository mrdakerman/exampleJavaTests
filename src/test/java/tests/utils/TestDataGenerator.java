package tests.utils;

import com.github.javafaker.Faker;
import tests.model.Student;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestDataGenerator {
    private static final Faker faker = new Faker();

    public static String generateName() {
        return faker.name().fullName();
    }

    public static int generateAge() {
        return faker.number().numberBetween(18, 30);
    }

    public static List<String> generateSubjects() {
        // Генерируем от 1 до 4 предметов
        int subjectsCount = faker.number().numberBetween(1, 5);
        
        return IntStream.range(0, subjectsCount)
            .mapToObj(i -> {
                switch (faker.number().numberBetween(0, 6)) {
                    case 0: return "Math";
                    case 1: return "Physics";
                    case 2: return "Chemistry";
                    case 3: return "Biology";
                    case 4: return "History";
                    default: return "Computer Science";
                }
            })
            .collect(Collectors.toList());
    }

    public static Student generateStudent() {
        return new Student(generateName(), generateAge(), generateSubjects());
    }
} 