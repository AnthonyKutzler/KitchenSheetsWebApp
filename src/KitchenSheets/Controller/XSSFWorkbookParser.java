package KitchenSheets.Controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class XSSFWorkbookParser {

    //Sheet Name Finals
    private static final String[] sheetsToDelete = {"SRDES", "TOTALS", "CLD25", "sample"};
    private static final List<String> rowsToDelete = Arrays.asList("QC regular", "QC sub", "QC organic");
    private static final File templateFile = new File("/home/gob/webSheets/hcsTemplate.xlsx");
    private final int LUNCH_OFFSET = 4;//(OFFSET * (day + 1)) loop 3
    private final int BRK_OFFSET = 3; //same as above
    private static final String OUTPUT_PATH = "/home/gob/webSheets/";
    private static final String[] daysByName = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final List<String> breakfastSheets = Arrays.asList("BRK", "SNK");
    private static final int BRK_COLUMN_WIDTH = 5302;
    private static final int LCH_COLUMN_WIDTH = 4022;
    private static final short ROW_HEIGHT = 799;
    private String workingBookName;
    private String day;
    private int dayInt;
    private int columnWidth;
    private XSSFWorkbook templateBook;
    private XSSFWorkbook workingBook;
    private CellStyle cellStyle;
    private CellStyle cloneCellStyle;
    private boolean lunch = true;


    public XSSFWorkbookParser(File file, String day){
        workingBookName = file.getName();
        this.day = day;
        for(int z = 0; z < daysByName.length; z++){
            if(daysByName[z].toLowerCase().equals(day.toLowerCase())) {
                dayInt = z;
                break;
            }
        }
        try {
            workingBook = new XSSFWorkbook(new FileInputStream(file));
            templateBook = new XSSFWorkbook(new FileInputStream(templateFile));
            //Delete sheets not in use
            for(String delete: sheetsToDelete)
                workingBook.removeSheetAt(workingBook.getSheetIndex(delete));
        }catch (Exception e){
            e.printStackTrace();
        }
        setCellStyle();
        /* //Test code for additional unknown sheets
        List<String> sheetNames = new ArrayList<>();
        for(int z = 0; z < workingBook.getNumberOfSheets(); z++){
            sheetNames.add(workingBook.getSheetName(z));
        }*/
        //parseCopy(items, columnAbsolutes);
    }

    public String parseCopy(String[][] items, int[] columnsAbsolutes){
        //Loops Sheets
        for(int z = 0; z < templateBook.getNumberOfSheets(); z++){
            int routeNumber = 2;
            String[] currentItems = items[0];
            XSSFSheet templateSheet = templateBook.getSheetAt(z);
            String sheetName = templateSheet.getSheetName();
            if(sheetName.equals("CLD25") || sheetName.equals("sample"))
                continue;
            XSSFSheet workingSheet = workingBook.getSheet(sheetName);
            int columnOffset = LUNCH_OFFSET + (3 * dayInt);
            int dayOffset = LUNCH_OFFSET;
            if(breakfastSheets.contains(templateSheet.getSheetName())) {
                lunch = false;
                columnOffset = BRK_OFFSET + (3 * dayInt);
                dayOffset = BRK_OFFSET;
                if (sheetName.equals("BRK"))
                    currentItems = items[1];
                else if(sheetName.equals("SNK"))
                    currentItems = items[2];
                else
                    continue;
            }
            Iterator<Row> workingRows = workingSheet.rowIterator();
            int skippedRowCount = 0;
            int rowCount = 0;
            //Loop Rows
            while(workingRows.hasNext()) {
                Row workingRow = workingRows.next();
                Row templateRow;
                //If second cell equals "TOTALS" end of sheet reached, break loop
                if(workingRow.getCell(1).getCellTypeEnum() == CellType.STRING){
                    if(workingRow.getCell(1).getStringCellValue().equals("TOTALS"))
                        break;
                }
                //If first row set Day and items
                if (workingRow.getRowNum() == 0) {
                    int rowOffset = 0;
                    templateRow = templateSheet.getRow(rowOffset);
                    templateRow.getCell(dayOffset).setCellValue(day);
                    parseItems(templateSheet, templateRow, dayOffset + 3, currentItems);
                }else if(rowsToDelete.contains(workingRow.getCell(1).getStringCellValue())){
                    skippedRowCount++;
                    continue;
                }else if(workingRow.getLastCellNum() >= columnOffset + 2){
                    if(workingRow.getCell(columnOffset + 2).getCellTypeEnum() == CellType.NUMERIC) {
                        if (workingRow.getCell(columnOffset + 2).getNumericCellValue() > 0) {
                            rowCount++;
                            int maxColumn = dayOffset + currentItems.length + 3;
                            int rowOffset = 0;
                            //int rowOffset = lunch ? 0 : 1;
                            templateRow = templateSheet.createRow((workingRow.getRowNum() - skippedRowCount) + rowOffset);
                            for (int y = 0; y < maxColumn; y++) {
                                if (y < dayOffset + 1) {
                                    Cell oldCell = workingRow.getCell(y);
                                    Cell newCell = templateRow.createCell(y, oldCell.getCellTypeEnum());
                                    cloneCell(newCell, oldCell);
                                } else if (y >= dayOffset + 1 && y < dayOffset + 3) {
                                    Cell oldCell = workingRow.getCell(y + (dayInt * 3));
                                    Cell newCell = templateRow.createCell(y, oldCell.getCellTypeEnum());
                                    cloneCell(newCell, oldCell);
                                } else {
                                    Cell cell = templateRow.createCell(y);
                                    cell.setCellStyle(cellStyle);
                                    templateSheet.setColumnWidth(y, columnWidth);
                                }
                            }
                            if(!lunch){
                                int tempRouteNumber = (int)templateRow.getCell(0).getNumericCellValue();
                                if(routeNumber != tempRouteNumber){
                                        routeNumber = tempRouteNumber;
                                        templateSheet.setRowBreak(templateRow.getRowNum() - 1);
                                }
                            }
                        } else {
                            skippedRowCount++;
                            continue;
                        }

                    }else {
                        skippedRowCount++;
                        continue;
                    }
                }else {
                    skippedRowCount++;
                    continue;
                }
                templateRow.setHeight(ROW_HEIGHT);
            }
            if(!lunch) {
                //templateSheet.removeRowBreak(templateSheet.getLastRowNum() - 3);
                templateSheet.getPrintSetup().setLandscape(true);
                templateSheet.setRepeatingRows(CellRangeAddress.valueOf("1:1"));
            }
            int printAreaColumn;
            switch(sheetName){
                //set print area column...
                //1, 6, 11 adult items
                //2, 3, 4, 8 All columns
                //2, 3, 4, 7, 8 at least 8
                //everyone else at least 6
                case "1":
                case "6":
                case "11":
                    if(columnsAbsolutes[1]  >= 6)
                        printAreaColumn = columnsAbsolutes[1] + dayOffset;
                    else
                        printAreaColumn = 6 + dayOffset + 2;
                    break;
                case "2":
                case "3":
                case "4":
                case "8":
                case "BRK":
                case "SNK":
                    printAreaColumn = dayOffset + 2 + currentItems.length;
                    break;
                case "7":
                    if(columnsAbsolutes[0] >= 8)
                        printAreaColumn = columnsAbsolutes[0] + dayOffset;
                    else
                        printAreaColumn = 8 + dayOffset + 2;
                    break;
                default:
                    if(columnsAbsolutes[0] >= 6)
                        //                lunch items   3=days columns 4=first 4 static lunch items
                        printAreaColumn = columnsAbsolutes[0] + dayOffset;
                    else
                        printAreaColumn = 6 + dayOffset + 2;
                    break;
            }
            templateBook.setPrintArea(z, 0, printAreaColumn, 0, rowCount);
        }
        String templateBookName = OUTPUT_PATH + workingBookName.
                substring(0, workingBookName.length() - 5) + "-" + day + ".xlsx";
        try {
            FileOutputStream workbookWrite = new FileOutputStream(templateBookName);
            templateBook.write(workbookWrite);
        }catch (Exception e){
            e.printStackTrace();
        }
        return templateBookName;

    }

    //Only used on first Row
    private void parseItems(XSSFSheet sheet, Row row, int offset, String[] items){
        int columnCount = items.length;
        if(lunch)
            columnWidth = LCH_COLUMN_WIDTH;
        else
            columnWidth = BRK_COLUMN_WIDTH;
        for(int z = offset; z < columnCount + offset; z++){
            Cell cell;
            cell = row.createCell(z, CellType.STRING);
            cell.setCellValue(items[z - offset]);
            cell.setCellStyle(cellStyle);
            sheet.setColumnWidth(z, columnWidth);
        }
    }

    private void cloneCell(Cell nCell, Cell oCell){
        cloneCellStyle.cloneStyleFrom(oCell.getCellStyle());
        nCell.setCellStyle(cloneCellStyle);
        CellType cellType = oCell.getCellTypeEnum();
        if(cellType == CellType.STRING)
            nCell.setCellValue(oCell.getStringCellValue());
        else if(cellType == CellType.NUMERIC)
            nCell.setCellValue(oCell.getNumericCellValue());
        else if(cellType == CellType.BLANK) {
            if(oCell.getNumericCellValue() == 0.0)
                nCell.setCellValue("");
            else
                nCell.setCellValue(oCell.getStringCellValue());
        }
    }

    private CellStyle setCellStyle(){
        //Cell Font
        cloneCellStyle = templateBook.createCellStyle();
        XSSFFont font = templateBook.createFont();
        font.setFontName("Comic Sans");
        font.setBold(false);
        font.setFontHeight(14);
        font.setFontName("Comic Sans");
        //Cell Style
        cellStyle = templateBook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        return cellStyle;
    }
}
