package kr.co.flywing.app;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.sl.draw.binding.ObjectFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by RangeWING on 2016-09-25.
 * Writes report file
 * # id name score score_code desc code [detail score]
 */
public class ReportManager {
    protected XSSFWorkbook workbook;
    protected XSSFSheet sheet;
    protected XSSFRow row;
    protected XSSFCell cell;
    protected int r = 0;
    protected int n = 1;
    protected static final String[][] titles = {
            {"#", "ID", "점수", "평가1", "실행점수", "설명점수", "설명", "코드", "Msg", "상세점수1", "상세점수2", "상세점수3", "상세점수4"},
            {null, "이름", "감점%", "평가2", "코드감점", "스타일점수", null, null, null, "상세점수5", "상세점수6", "상세점수7"}
    };
    protected static final String[] COLS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private final int EXEC_N = Constant.getInt(Constant.SCORE_EXEC_N);
    private final int SCORE_EXEC = Constant.getInt(Constant.SCORE_EXEC);
    private final int SCORE_DESC = Constant.getInt(Constant.SCORE_DESC);
    private final int SCORE_STYLE = Constant.getInt(Constant.SCORE_STYLE);

    public ReportManager(){
        workbook = new XSSFWorkbook(XSSFWorkbookType.XLSX);
        sheet = workbook.createSheet("sheet1");
        writeTitle();
    }

    protected void writeTitle(){
        row = sheet.createRow(r++);
        int c = 0;
        for(String title : titles[0]) {
            cell = row.createCell(c++);
            cell.setCellValue(title);
        }
        row = sheet.createRow(r++);
        c = 0;
        for(String title : titles[1]) {
            cell = row.createCell(c++);
            if(title != null)
                cell.setCellValue(title);
            else{
                sheet.addMergedRegion(new CellRangeAddress(0,1,c-1,c-1));
            }
        }

    }

    public void write(Document doc){
        if(doc == null) return;
        write(doc.id, doc.name, CGrader.calcScore(doc.scores), doc.desc, doc.code, doc.msg, doc.scores, doc.execOutput);
    }

    public void write(String id, String name, double score, String desc, String code, String msg, boolean[] scores, List<String> execOutput){
        int c = 0;
        String scoreFormula1 = "SUM(E%d, F%d, F%d) - E%d * E%d * 0.01";
        String scoreFormula2 = "(%s)-(%s)*C%d*0.01";
        String evalFormula = "\"실행 결과: \"&(E%d-E%d*E%d*0.01)&\"/" + SCORE_EXEC  + "점 (%d/" + EXEC_N + "개 성공)\n" +
                "설명 점수: \"&F%d&\"/" + SCORE_DESC + "점 \"&IF(F%d<" + SCORE_DESC + ", \"(설명 부족)\", \"\")&\"\n" +
                "코드 스타일 점수: \"&F%d&\"/" + SCORE_STYLE + "점 \"&IF(F%d<" + SCORE_STYLE + ", \"(주석 부족)\", \"\")";

        Object data[][] = {
                {n++, id, scoreFormula2, evalFormula, (int)score, "", desc, code},
                {null, name, 0, "", 0, "", null, null}
        };
        //first row
        row = sheet.createRow(r++);

        int execOK = 0;
        if(scores != null)
            for(boolean bs : scores)
                if(bs) execOK++;

        String sum = String.format(scoreFormula1, r, r, r+1, r, r+1);
        data[0][2] = String.format(scoreFormula2, sum, sum, r+1);
        data[0][3] = String.format(evalFormula, r, r, r+1, execOK, r, r, r+1, r+1);


        for(Object d : data[0]) {
            cell = row.createCell(c++);
            if(d == null) continue;
            else if(d.toString().matches("^-?\\d+$")) cell.setCellValue((int)d);
            else if(c == 3 || c == 4) cell.setCellFormula(d.toString());
            else cell.setCellValue(d.toString());
        }
        row = sheet.createRow(r++);
        c = 0;
        for(Object d : data[1]) {
            cell = row.createCell(c++);
            if(d != null) {
                if (d.toString().matches("^-?\\d+$")) cell.setCellValue((int) d);
                else cell.setCellValue(d.toString());
            }else{
                sheet.addMergedRegion(new CellRangeAddress(r-2,r-1,c-1,c-1));
            }
        }

        row = sheet.getRow(r-2);
        cell = row.createCell(c++);
        if(msg.length() > 30000) {
            int m = 0;
            while (msg.length() > 30000) {
                cell.setCellValue(msg.substring(m * 30000, 30000 * (m + 1)));
                sheet.addMergedRegion(new CellRangeAddress(r-2,r-1,c-1,c-1));
                m++;
                msg = msg.substring(30000);
                cell = row.createCell(c++);
            }
        }
        cell.setCellValue(msg);
        sheet.addMergedRegion(new CellRangeAddress(r-2,r-1,c-1,c-1));

        int cc = c;
        if(scores != null) {
            if(scores.length > 0) {
                for (int i = 0; i < scores.length / 2 + 1; i++) {
                    cell = row.createCell(c++);
                    cell.setCellValue(scores[i] + "\n" + execOutput.get(i));
                }
                row = sheet.getRow(r-1);
                c = cc;
                for (int i = scores.length/2+1; i < scores.length; i++) {
                    cell = row.createCell(c++);
                    cell.setCellValue(scores[i] + "\n" + execOutput.get(i));
                }
            }
        }else{
            cell = row.createCell(c++);
            cell.setCellValue("NULL");
        }
    }

    public static String cellNumber(int r, int c){
        return COLS[c] + r;
    }

    public void writeToFile(File file) throws IOException{
        FileOutputStream fos = new FileOutputStream(file);
        workbook.write(fos);
        fos.close();
    }
}
