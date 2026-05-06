package com.sdd.exporters;

import com.sdd.core.TestCaseModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ExcelExporter - Exports test cases to a formatted .xlsx workbook.
 * Creates Summary + UI + API + Accessibility + All sheets.
 */
public class ExcelExporter {

    public static void export(List<TestCaseModel> tcs, String path) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();
        createSummarySheet(wb, tcs);
        createSheet(wb, "UI Test Cases",
            tcs.stream().filter(t -> t.getType().equals("UI")).collect(Collectors.toList()),
            new byte[]{0x00, 0x78, 0x6E});
        createSheet(wb, "API Test Cases",
            tcs.stream().filter(t -> t.getType().equals("API")).collect(Collectors.toList()),
            new byte[]{0x1A, 0x56, 0x76});
        createSheet(wb, "Accessibility Test Cases",
            tcs.stream().filter(t -> t.getType().equals("ACCESSIBILITY")).collect(Collectors.toList()),
            new byte[]{0x4A, 0x23, 0x5E});
        createSheet(wb, "All Test Cases", tcs, new byte[]{0x27, 0x4E, 0x13});
        try (FileOutputStream fos = new FileOutputStream(path)) { wb.write(fos); }
        wb.close();
        System.out.println("Excel exported: " + path);
    }

    private static void createSummarySheet(XSSFWorkbook wb, List<TestCaseModel> tcs) {
        XSSFSheet s = wb.createSheet("Summary");
        s.setColumnWidth(0, 7000); s.setColumnWidth(1, 4000);
        XSSFCellStyle title = wb.createCellStyle();
        XSSFFont tf = wb.createFont(); tf.setBold(true); tf.setFontHeightInPoints((short)14);
        title.setFont(tf);
        Row r0 = s.createRow(0); Cell c0 = r0.createCell(0);
        c0.setCellValue("SDD Framework - Test Case Summary"); c0.setCellStyle(title);
        s.createRow(1).createCell(0).setCellValue("Spec-Driven Development | Auto-Generated");
        s.createRow(2);
        long ui = tcs.stream().filter(t->t.getType().equals("UI")).count();
        long api = tcs.stream().filter(t->t.getType().equals("API")).count();
        long a11y = tcs.stream().filter(t->t.getType().equals("ACCESSIBILITY")).count();
        String[][] data = {
            {"Total Test Cases", String.valueOf(tcs.size())},
            {"UI Test Cases", String.valueOf(ui)},
            {"API Test Cases", String.valueOf(api)},
            {"Accessibility TCs", String.valueOf(a11y)},
            {"Critical", String.valueOf(tcs.stream().filter(t->t.getPriority().equals("Critical")).count())},
            {"High",     String.valueOf(tcs.stream().filter(t->t.getPriority().equals("High")).count())},
            {"Medium",   String.valueOf(tcs.stream().filter(t->t.getPriority().equals("Medium")).count())},
            {"Low",      String.valueOf(tcs.stream().filter(t->t.getPriority().equals("Low")).count())}
        };
        int rn = 3;
        for (String[] row : data) {
            Row r = s.createRow(rn++);
            r.createCell(0).setCellValue(row[0]);
            r.createCell(1).setCellValue(row[1]);
        }
    }

    private static void createSheet(XSSFWorkbook wb, String name,
                                     List<TestCaseModel> tcs, byte[] color) {
        if (tcs.isEmpty()) return;
        XSSFSheet s = wb.createSheet(name);
        int[] widths = {3000,3000,4500,9000,3000,3000,4000,12000,13000,10000,12000,4000,3000};
        for (int i = 0; i < widths.length; i++) s.setColumnWidth(i, widths[i]);

        XSSFCellStyle hStyle = wb.createCellStyle();
        hStyle.setFillForegroundColor(new XSSFColor(color, null));
        hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont hf = wb.createFont(); hf.setBold(true);
        hf.setColor(new XSSFColor(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF}, null));
        hf.setFontHeightInPoints((short)11); hStyle.setFont(hf);
        hStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle wrapStyle = wb.createCellStyle();
        wrapStyle.setWrapText(true); wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);
        XSSFCellStyle altStyle = wb.createCellStyle();
        altStyle.setWrapText(true); altStyle.setVerticalAlignment(VerticalAlignment.TOP);
        altStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xF5,(byte)0xF5,(byte)0xF5}, null));
        altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] headers = {"TC ID","Ticket ID","Req ID","Title","Type","Priority","Module",
                            "Preconditions","Test Steps","Test Data","Expected Result","Actual Result","Status"};
        Row hr = s.createRow(0); hr.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            Cell c = hr.createCell(i); c.setCellValue(headers[i]); c.setCellStyle(hStyle);
        }

        int rn = 1;
        for (TestCaseModel tc : tcs) {
            Row r = s.createRow(rn); r.setHeightInPoints(70);
            XSSFCellStyle rs = (rn % 2 == 0) ? altStyle : wrapStyle;
            String[] vals = {tc.getTcId(), tc.getTicketId(), tc.getRequirementId(), tc.getTitle(),
                tc.getType(), tc.getPriority(), tc.getModule(), tc.getPreconditions(),
                tc.getSteps(), tc.getTestData(), tc.getExpectedResult(),
                tc.getActualResult(), tc.getStatus()};
            for (int i = 0; i < vals.length; i++) {
                Cell c = r.createCell(i); c.setCellValue(vals[i] != null ? vals[i] : ""); c.setCellStyle(rs);
            }
            rn++;
        }
        s.createFreezePane(0, 1);
    }
}
