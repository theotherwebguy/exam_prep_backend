package org.backend.examprep_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TempStudentDTO extends UserDetailDto{

    private Boolean isApproved = false;
    private Long classId;
}
