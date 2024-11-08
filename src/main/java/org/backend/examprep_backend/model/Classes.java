package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classesId;

    @Column(length = 255, unique = true)
    private String className;

    @Column(length = 255)
    private String classDescription;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users lecturer;

    @ManyToMany(mappedBy = "studentClasses")
    private Set<Users> students = new HashSet<>();

    // Utility method to remove a student
    public void removeStudent(Users student) {
        this.students.remove(student);
        student.getStudentClasses().remove(this);
    }
}
