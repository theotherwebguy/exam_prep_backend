package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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


    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users lecturer;

    @OneToMany(mappedBy = "studentClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Users> students = new ArrayList<>();

}