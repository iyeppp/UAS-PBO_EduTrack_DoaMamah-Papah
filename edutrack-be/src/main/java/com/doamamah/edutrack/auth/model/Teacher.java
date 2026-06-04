package com.doamamah.edutrack.auth.model;

import jakarta.persistence.*;

/**
 * Entity JPA untuk Teacher — subclass dari User.
 * Discriminator value: "TEACHER"
 */
@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

    private String teacherId;
    private String specialization;
    private int totalCourses;

    public Teacher() {
        super();
    }

    public Teacher(String username, String password, String fullName, String email,
                   String teacherId, String specialization, int totalCourses) {
        super(username, password, fullName, email);
        this.teacherId = teacherId;
        this.specialization = specialization;
        this.totalCourses = totalCourses;
    }

    // --- GETTERS & SETTERS ---

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getTotalCourses() { return totalCourses; }
    public void setTotalCourses(int totalCourses) { this.totalCourses = totalCourses; }

    @Override
    @Transient
    public String getRole() {
        return "TEACHER";
    }
}
