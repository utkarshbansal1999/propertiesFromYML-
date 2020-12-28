package com.example.propertiesToExcel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class PropertiesToExcelApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PropertiesToExcelApplication.class, args);

        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("properties");

        File file = new File("/home/utkarshbansal/Downloads/JSONS/test");
        Scanner sc = new Scanner(file);

        int groupsOfInitialSpace = 2;
        int previousKeyIndex = -1;
        int currentKeyIndex;
        int totalNumberOfKeys = 0;
        char prevLineLastCharacter = '.';

        String value = "cx-portal-service";
        List<String> levels = new ArrayList<>();
        StringBuilder key;

        while (sc.hasNextLine()) {

            String str = sc.nextLine();

            StringBuilder result = new StringBuilder();
            int initialSpaceCount = 0;
            boolean initialSpaceEnded = false;
            boolean colonPresent = false;

            if (str.length() == 0) continue;

            if (prevLineLastCharacter == '\\') {
                prevLineLastCharacter = str.charAt(str.length() - 1);
                continue;
            }
            for (int i = 0; i < str.length(); i++) {

                /*if (initialSpaceEnded) {
                    if (str.charAt(i) == ':') {
                        colonPresent = true;
                        break;
                    }
                    result.append(str.charAt(i));
                } else {
                    if (str.charAt(i) == '\\') break;
                    if (str.charAt(i) == '-' )
                }*/


                if (str.charAt(i) == ':' || str.charAt(i) == '=') {
                    colonPresent = true;
                    break;
                }
                if ((str.charAt(i) == '-' || str.charAt(i) == '\\' || str.charAt(i) == '#') && !initialSpaceEnded) break;
                if (str.charAt(i) == ' ' && !initialSpaceEnded) initialSpaceCount++;
                else {
                    initialSpaceEnded = true;
                    result.append(str.charAt(i));
                }
            }
            prevLineLastCharacter = str.charAt(str.length() - 1);
            if (!initialSpaceEnded && !colonPresent) continue;
            /*System.out.println(initialSpaceCount);
            System.out.println(String.valueOf(result));*/
            currentKeyIndex = initialSpaceCount / groupsOfInitialSpace;
            if (currentKeyIndex - previousKeyIndex == 1) {
                /*System.out.println("Cur : " + currentKeyIndex);
                System.out.println("Before adding : " + levels.size());*/
                if (levels.size() > currentKeyIndex) {
                    levels.set(currentKeyIndex, String.valueOf(result));
                } else {
                    levels.add(String.valueOf(result));
                }
//                System.out.println("After Adding : " + levels.size());
                previousKeyIndex = currentKeyIndex;
            } else if (currentKeyIndex <= previousKeyIndex) {
                key = new StringBuilder(levels.get(0));
                for (int i = 1; i <= previousKeyIndex; i++) key.append(".").append(levels.get(i));
                levels.set(currentKeyIndex, String.valueOf(result));
                previousKeyIndex = currentKeyIndex;

                Row row = sheet.createRow(totalNumberOfKeys++);
                Cell cell = row.createCell(0);
                cell.setCellValue(String.valueOf(key));

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(value);

            }
        }
        key = new StringBuilder(levels.get(0));
        for (int i = 1; i <= previousKeyIndex; i++) key.append(".").append(levels.get(i));
        Row row = sheet.createRow(totalNumberOfKeys);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.valueOf(key));

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(value);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        book.write(new FileOutputStream("properties.xlsx"));
        book.close();
    }

}
