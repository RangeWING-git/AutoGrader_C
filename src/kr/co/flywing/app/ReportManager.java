package kr.co.flywing.app;

import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by RangeWING on 2016-09-25.
 * Writes report file
 * # id name score score_code desc code [detail score]
 */
public class ReportManager {
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    XSSFRow row;
    XSSFCell cell;
    int r = 0;
    int n = 1;
    String[] titles = {"#", "ID", "이름", "점수", "실행점수", "설명점수", "코드스타일점수", "설명", "코드", "Msg", "상세점수"};

    public ReportManager(){
        workbook = new XSSFWorkbook(XSSFWorkbookType.XLSX);
        sheet = workbook.createSheet("sheet1");
        writeTitle();
    }

    protected void writeTitle(){
        row = sheet.createRow(r++);
        int c = 0;
        for(String title : titles) {
            cell = row.createCell(c++);
            cell.setCellValue(title);
        }
    }

    public void write(Document doc){
        if(doc == null) return;
        write(doc.id, doc.name, CGrader.calcScore(doc.scores), doc.desc, doc.code, doc.msg, doc.scores);
    }

    public void write(String id, String name, double score, String desc, String code, String msg, boolean[] scores){
        int c = titles.length-1;
        row = sheet.createRow(r++);
        cell = row.createCell(0);
        cell.setCellValue(n++);
        cell = row.createCell(1);
        cell.setCellValue(id);
        cell = row.createCell(2);
        cell.setCellValue(name);
        cell = row.createCell(4);
        cell.setCellValue(score);
        cell = row.createCell(7);
        cell.setCellValue(desc);
        cell = row.createCell(8);
        cell.setCellValue(code);
        cell = row.createCell(9);
        cell.setCellValue(msg);
        if(scores != null) {
            for (boolean s : scores) {
                cell = row.createCell(c++);
                cell.setCellValue(s);
            }
        }else{
            cell = row.createCell(9);
            cell.setCellValue("NULL");
        }
    }



    public void writeToFile(File file) throws IOException{
        FileOutputStream fos = new FileOutputStream(file);
        workbook.write(fos);
        fos.close();
    }
}
