package com.doamamah.edutrack.auth.model;

import jakarta.persistence.*;

/**
 * Entity JPA untuk Student — subclass dari User.
 * Discriminator value: "STUDENT"
 */
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    private String studentId;
    private int enrolledCourses;

    public Student() {
        super();
    }

    public Student(String username, String password, String fullName, String email,
                   String studentId, int enrolledCourses) {
        super(username, password, fullName, email);
        this.studentId = studentId;
        this.enrolledCourses = enrolledCourses;
    }

    // --- GETTERS & SETTERS ---

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public int getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(int enrolledCourses) { this.enrolledCourses = enrolledCourses; }

    @Override
    @Transient
    public String getRole() {
        return "STUDENT";
    }
}
