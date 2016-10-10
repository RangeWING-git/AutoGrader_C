package kr.co.flywing.app;

import rcc.h2tlib.parser.H2TParser;
import rcc.h2tlib.parser.HWPMeta;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by RangeWING on 2016-09-14.
 */
public class HWPHandler {
    H2TParser h2TParser;

    public HWPHandler(){
        h2TParser = new H2TParser();
    }

    public String getText(String file) throws NotHWPFileException{
        StringBuilder sb = new StringBuilder();
        HWPMeta meta = new HWPMeta();
        if(!h2TParser.GetText(file, meta, sb)) throw new NotHWPFileException(file);
        return sb.toString();
    }

    public String getCode(String file) throws NotHWPFileException{
        return parse(getText(file)).code;
    }

    public Document getDocument(File file) throws NotHWPFileException{
        String text = getText(file.getAbsolutePath());
        Document doc = null;
        try {
            doc = parse(text);
            String fileName = file.getName();
            doc.id = FileHandler.getIdFromFileName(fileName);
            doc.name = FileHandler.getNameFromFileName(fileName);
        }catch(Exception e){
            System.err.println(file.getAbsolutePath());
            e.printStackTrace();
        }
        return doc;
    }

    private Document parse(String text) throws IndexOutOfBoundsException{
        if(text == null) return null;

        Document doc = new Document();
        int idx1 = text.indexOf("소스코드 :");
        int idx2 = text.indexOf("#", idx1);
        int idx3 = text.indexOf("/", idx1);

        int idxd1 = text.indexOf("소스코드 설명(5줄 내외로 작성) :");

        doc.id = null;
        doc.name = null;
        doc.desc = text.substring(idxd1+20, idx1);
        doc.code = text.substring(idx2>0 ? (idx3>0 ? Math.min(idx2, idx3) : idx2) : idx3);
        return doc;
    }

    public boolean writeCode(String input, String output){
        try {
            FileWriter fw = new FileWriter(output);
            fw.write(getCode(input));
            fw.close();
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean writeText(String input, String output){
        try {
            FileWriter fw = new FileWriter(output);
            fw.write(getText(input));
            fw.close();
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public class NotHWPFileException extends Exception{
        public NotHWPFileException(String filename){
            super(filename + " is not a HWP file");
        }
    }


}
