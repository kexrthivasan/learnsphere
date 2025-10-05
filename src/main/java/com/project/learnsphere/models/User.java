package com.project.learnsphere.models;
import com.project.learnsphere.models.course.Course;
import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user entity, serving as the central model for authentication and roles.
 */
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Course> coursesInstructing = new HashSet<>();


}