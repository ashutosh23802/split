package org.tiaa.split.controller;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserSegregationController {

    @GetMapping("/user")
    public void  parseExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook(new FileInputStream(new File("src/main/resources/SPLIT_ACCESS_FORMAT.xlsx")));

        // get list if ids from sheet AD_GROUP
        List<String> racdIdsFromAdGroupExcel = new ArrayList<>();
        workbook.getSheet("AD_GROUP").forEach( row -> {
            racdIdsFromAdGroupExcel.add(row.getCell(0).getStringCellValue());
        });

        //Get email ids for racfids from AD_GROUP EXCEL


    }
}
