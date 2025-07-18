package org.tiaa.in.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private static final Logger logger = LoggerFactory.getLogger(ExcelController.class);

    @Value("${split.data.folder:split-data}")
    private String splitDataFolder;

    @PostMapping("/process")
    public ResponseEntity<?> processExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload a file");
            }

            // Create split-data directory if it doesn't exist
            Path dataDirectory = Paths.get(splitDataFolder);
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
                logger.info("Created directory: {}", dataDirectory);
            }

            // Generate unique filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = "excel_" + timestamp + fileExtension;

            // Save file to split-data folder
            Path filePath = dataDirectory.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);
            logger.info("Saved file: {}", filePath);

            // Process the Excel file
            Workbook workbook = WorkbookFactory.create(filePath.toFile());
            Sheet sheet = workbook.getSheetAt(0);

            // Get headers
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            headerRow.forEach(cell -> headers.add(getCellValueAsString(cell)));

            // Get data rows
            List<List<String>> rows = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    List<String> rowData = new ArrayList<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        rowData.add(getCellValueAsString(cell));
                    }
                    rows.add(rowData);
                }
            }

            workbook.close();

            Map<String, Object> response = new HashMap<>();
            response.put("headers", headers);
            response.put("rows", rows);
            response.put("filename", newFilename);
            response.put("filepath", filePath.toString());
            response.put("timestamp", timestamp);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing file", e);
            return ResponseEntity.internalServerError()
                .body("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/read/{filename}")
    public ResponseEntity<?> readExcel(@PathVariable String filename) {
        try {
            File file = new File(splitDataFolder + File.separator + filename);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);

            // Get headers
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            headerRow.forEach(cell -> headers.add(getCellValueAsString(cell)));

            // Get data rows
            List<List<String>> rows = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    List<String> rowData = new ArrayList<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        rowData.add(getCellValueAsString(cell));
                    }
                    rows.add(rowData);
                }
            }

            workbook.close();

            Map<String, Object> response = new HashMap<>();
            response.put("headers", headers);
            response.put("rows", rows);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error reading file: " + e.getMessage());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue().toString();
                    }
                    // Format numbers without scientific notation
                    return String.format("%.2f", cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e) {
                        return cell.getStringCellValue();
                    }
                default:
                    return "";
            }
        } catch (Exception e) {
            logger.warn("Error reading cell value", e);
            return "";
        }
    }
}
