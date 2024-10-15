package org.backend.examprep_backend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentExcelParserService {

    // Method to extract student details from an Excel file
    public List<Users> extractStudentsFromExcel(MultipartFile file, Role studentRole,  Long classesId) throws Exception {
        List<Users> students = new ArrayList<>();
        String defaultPassword = "default123";  // Set default password for students

        // Get the input stream from the uploaded Excel file
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

            // Iterate through each row in the sheet
            for (Row row : sheet) {
                if (row.getRowNum() == 0) { // Skip the header row
                    continue;
                }

                // Extract and set student details from each row
                Users student = new Users();
                student.setFullNames(getCellValueAsString(row.getCell(0))); // Full names
                student.setEmail(getCellValueAsString(row.getCell(1))); // Email
                student.setTitle(getCellValueAsString(row.getCell(2))); // Title
                student.setSurname(getCellValueAsString(row.getCell(3))); // Surname
                student.setContactNumber(getCellValueAsString(row.getCell(4))); // Contact number
                student.setPassword(defaultPassword); // Default password
                student.setRole(studentRole); // Assign the role

                // Add the student to the list
                Classes studentClass = new Classes();
                studentClass.setClassesId(classesId);
                student.setStudentClass(studentClass);

                students.add(student);
            }

            // Close the workbook after processing
            workbook.close();
        }

        return students; // Return the list of students extracted from the Excel file
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

