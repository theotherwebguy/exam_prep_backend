package org.backend.examprep_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class TempStudentDTO {

    private Long id;
    private String email;
    private String password;
    private String title;
    private String fullNames;
    private String surname;
    private String contactNumber;
    private String role;
    private byte[] profileImage;
    private Boolean isApproved = false;
    private Long classId;
}
