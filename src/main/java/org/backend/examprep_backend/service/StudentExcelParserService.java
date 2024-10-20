package org.backend.examprep_backend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.model.Role;
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
    private PasswordEncoder passwordEncoder;
    // Method to extract student details from an Excel file
    public List<Users> extractStudentsFromExcel(MultipartFile file, Role studentRole,  Long classesId) throws Exception {
        List<Users> students = new ArrayList<>();
        String defaultPassword = "default123";

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

                // Create a new student object and populate fields
                Users student = new Users();
                student.setFullNames(getCellValueAsString(row.getCell(columnIndexMap.get("Full Names"))));
                student.setEmail(getCellValueAsString(row.getCell(columnIndexMap.get("Email"))));
                student.setTitle(getCellValueAsString(row.getCell(columnIndexMap.get("Title"))));
                student.setSurname(getCellValueAsString(row.getCell(columnIndexMap.get("Surname"))));
                student.setContactNumber(getCellValueAsString(row.getCell(columnIndexMap.get("Contact Number"))));
                student.setPassword(passwordEncoder.encode(defaultPassword));
                student.setRole(studentRole);


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

    // Helper method to map column headers to their indexes
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

