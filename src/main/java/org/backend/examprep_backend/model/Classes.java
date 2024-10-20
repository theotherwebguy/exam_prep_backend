package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classesId;

    @Column(length = 255)
    private String className;

    @Column(length = 255)
    private String classDescription;
    private LocalDate startDate;
    private LocalDate endDate;


    @ManyToOne(fetch = FetchType.EAGER) // Adjust fetch type if needed
    @JoinColumn(name = "course_id", nullable = false) // Foreign key
    private Course course; // Add this field

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users lecturer;

    @OneToMany(mappedBy = "studentClass", fetch = FetchType.LAZY)
    private Set<Users> students;


}