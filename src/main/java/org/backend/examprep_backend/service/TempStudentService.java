package org.backend.examprep_backend.service;

import jakarta.transaction.Transactional;
import org.backend.examprep_backend.dto.TempStudentDTO;
import org.backend.examprep_backend.dto.UserDto;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.TempoStudent;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.ClassRepository;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.repository.TempStudentRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TempStudentService {

    @Autowired
    private TempStudentRepository tempStudentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(TempStudentService.class);

    // Register a temporary student with a specific class
    @Transactional
    public TempoStudent registerTempStudent(TempStudentDTO tempStudentDto) {
        // Check if email already exists in users or temporary students
        if (userRepository.findByEmail(tempStudentDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists in the system.");
        }
        if (tempStudentRepository.findByEmail(tempStudentDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("A temporary student with this email already exists.");
        }

        // Retrieve and set class for the temporary student
        Classes studentClass = classRepository.findById(tempStudentDto.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found with ID: " + tempStudentDto.getClassId()));

        TempoStudent tempStudent = new TempoStudent();
        tempStudent.setClassId(studentClass.getClassesId());
        tempStudent.setEmail(tempStudentDto.getEmail());
        tempStudent.setPassword(passwordEncoder.encode(tempStudentDto.getPassword()));
        tempStudent.setTitle(tempStudentDto.getTitle());
        tempStudent.setFullNames(tempStudentDto.getFullNames());
        tempStudent.setSurname(tempStudentDto.getSurname());
        tempStudent.setContactNumber(tempStudentDto.getContactNumber());
        tempStudent.setApproved(tempStudentDto.getIsApproved());

        // Retrieve and assign the role of "STUDENT"
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new IllegalArgumentException("Role 'STUDENT' not found"));
        tempStudent.setRole(studentRole);

        logger.info("Registering new temporary student: {}", tempStudent);
        return tempStudentRepository.save(tempStudent);
    }

    // Approve a temporary student
    @Transactional
    public ResponseEntity<?> approveTempStudent(Long tempStudentId, boolean isApproved) {
        logger.info("Attempting to approve TempStudent with ID: {}", tempStudentId);

        TempoStudent tempStudent = tempStudentRepository.findById(tempStudentId)
                .orElseThrow(() -> new IllegalArgumentException("Temporary student not found"));
        logger.info("Retrieved TempStudent: {}", tempStudent);

        if (tempStudent.getApproved()) {
            logger.warn("TempStudent with ID {} is already approved.", tempStudentId);
            return ResponseEntity.badRequest().body("Student is already approved.");
        }

        if (!isApproved) {
            logger.info("Approval denied for TempStudent with ID: {}", tempStudentId);
            return ResponseEntity.ok("Approval denied for temporary student with ID " + tempStudentId + ".");
        }

        // Transfer data to a new Users entity
        Users user = createUserFromTempStudent(tempStudent);
        userRepository.save(user);
        logger.info("User saved successfully with ID: {}", user.getId());

        // Remove the temporary student entry
        tempStudentRepository.delete(tempStudent);
        logger.info("Deleted TempStudent with ID: {}", tempStudentId);

        // Convert the Users entity to a UserDto and return
        UserDto userDto = mapUserToUserDto(user);
        logger.info("Returning response DTO: {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    private Users createUserFromTempStudent(TempoStudent tempStudent) {
        Users user = new Users();
        user.setEmail(tempStudent.getEmail());
        user.setPassword(tempStudent.getPassword());
        user.setTitle(tempStudent.getTitle());
        user.setFullNames(tempStudent.getFullNames());
        user.setSurname(tempStudent.getSurname());
        user.setContactNumber(tempStudent.getContactNumber());
        user.setRole(tempStudent.getRole());

        // Set class association for the new user
        Classes studentClass = classRepository.findById(tempStudent.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found with ID: " + tempStudent.getClassId()));
        user.getStudentClasses().add(studentClass);
        logger.info("Added class to user. User's classes now: {}", user.getStudentClasses());

        return user;
    }

    private UserDto mapUserToUserDto(Users user) {
        logger.info("Mapping User to UserDto for User ID: {}", user.getId());
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setTitle(user.getTitle());
        userDto.setFullNames(user.getFullNames());
        userDto.setSurname(user.getSurname());
        userDto.setContactNumber(user.getContactNumber());
        userDto.setRole(user.getRole().getName());
        userDto.setProfileImage(user.getProfileImage());

        // Map class IDs only, omitting sensitive data like password
        List<Long> classIds = user.getStudentClasses().stream()
                .map(Classes::getClassesId)
                .collect(Collectors.toList());
        userDto.setClassIds(classIds);
        logger.info("Mapped class IDs to UserDto: {}", classIds);

        return userDto;
    }

    // Get all temporary students
    @Transactional
    public List<TempoStudent> getAllTempStudents() {
        return tempStudentRepository.findAll();
    }

    // Get a temporary student by ID
    public TempoStudent getTempStudentById(Long id) {
        return tempStudentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Temporary student not found"));
    }

    // Update a temporary student by ID
    @Transactional
    public TempoStudent updateTempStudent(Long id, TempStudentDTO tempStudentDto) {
        TempoStudent tempStudent = tempStudentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Temporary student not found"));

        tempStudent.setEmail(tempStudentDto.getEmail());
        tempStudent.setTitle(tempStudentDto.getTitle());
        tempStudent.setFullNames(tempStudentDto.getFullNames());
        tempStudent.setSurname(tempStudentDto.getSurname());
        tempStudent.setContactNumber(tempStudentDto.getContactNumber());

        // Update class association
        Classes studentClass = classRepository.findById(tempStudentDto.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found with ID: " + tempStudentDto.getClassId()));
        tempStudent.setClassId(studentClass.getClassesId());

        logger.info("Updated temporary student: {}", tempStudent);
        return tempStudentRepository.save(tempStudent);
    }

    // Delete a temporary student by ID
    @Transactional
    public void deleteTempStudent(Long id) {
        TempoStudent tempStudent = tempStudentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Temporary student not found"));
        tempStudentRepository.delete(tempStudent);
        logger.info("Deleted temporary student with ID: {}", id);
    }
}
