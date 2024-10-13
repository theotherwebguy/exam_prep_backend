package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

import java.util.Date;
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

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "userId")  // Lecturer reference
    private Users lecturer;


    private Date startDate;
    private Date endDate;

}