package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long domainId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Column(nullable = false)
    private String domainName;

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL)
    private List<Topic> topics; // Topics under the domain
}
