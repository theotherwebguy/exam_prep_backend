package org.backend.examprep_backend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.repository.ClassRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentExcelParserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    // Method to extract student details from an Excel file
    public List<Users> extractStudentsFromExcel(MultipartFile file, Role studentRole,  Long classesId) throws Exception {
        List<Users> students = new ArrayList<>();
        String defaultPassword = "default123";

        // Get class by ID
        Classes studentClass = classRepository.findById(classesId)
                .orElseThrow(() -> new RuntimeException("Class not found with ID: " + classesId));

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Get the header row and map column names to their respective indexes
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnIndexMap = mapColumnHeaders(headerRow);

            // Iterate through each data row (skip the header row)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) continue; // Skip empty rows

                String email = getCellValueAsString(row.getCell(columnIndexMap.get("Email")));

                // Create a new student object and populate fields
                Users student = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            // Create a new student if not found
                            Users newStudent = new Users();
                            newStudent.setFullNames(getCellValueAsString(row.getCell(columnIndexMap.get("Full Names"))));
                            newStudent.setEmail(email);
                            newStudent.setTitle(getCellValueAsString(row.getCell(columnIndexMap.get("Title"))));
                            newStudent.setSurname(getCellValueAsString(row.getCell(columnIndexMap.get("Surname"))));
                            newStudent.setContactNumber(getCellValueAsString(row.getCell(columnIndexMap.get("Contact Number"))));
                            newStudent.setPassword(passwordEncoder.encode(defaultPassword));
                            newStudent.setRole(studentRole);
                            return newStudent;
                        });


                // Add the class to the student's class set
                student.getStudentClasses().add(studentClass);
                studentClass.getStudents().add(student);

                students.add(student);
            }

            // Close the workbook after processing
            workbook.close();
        }

        return students;
    }

    private Map<String, Integer> mapColumnHeaders(Row headerRow) {
        Map<String, Integer> columnIndexMap = new HashMap<>();

        for (Cell cell : headerRow) {
            String header = getCellValueAsString(cell).trim();
            columnIndexMap.put(header, cell.getColumnIndex());
        }

        return columnIndexMap;
    }
    // Helper method to handle different cell types and return as String
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // Return date as string if it's a date
                } else {
                    return String.valueOf((long) cell.getNumericCellValue()); // Convert numeric to long to avoid decimals
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}

