package tests.model;

import java.util.List;

public class Student {
    private Long id;
    private String name;
    private int age;
    private List<String> subjects;

    public Student() {
    }

    public Student(String name, int age, List<String> subjects) {
        this.name = name;
        this.age = age;
        this.subjects = subjects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
} 