package com.project.learnsphere.models.course;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sub_modules")
public class SubModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
}
