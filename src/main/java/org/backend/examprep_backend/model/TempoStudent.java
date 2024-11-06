package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tempo_student")
@Getter
@Setter
public class TempoStudent extends Users{

    @Column(nullable = false)
    private Long classId;

    @Column(nullable = false)
    private Boolean approved = false;
}
