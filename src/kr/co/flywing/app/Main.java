package kr.co.flywing.app;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by RangeWING on 2016-09-14.
 */
public class Main {
    public static void main(String[] args){
        try {
            Constant.init();
        }catch(Constant.NotSupportedOSException e){
            e.printStackTrace();
            System.exit(1);
        }catch (IOException e){
            e.printStackTrace();
            System.exit(2);
        }

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyMMddhhmmss");

        File inputPath = Constant.getPathAsFile(Constant.PATH_INPUT);
        File outputPath = Constant.getPathAsFile(Constant.PATH_OUTPUT);
        File execPath = Constant.getPathAsFile(Constant.PATH_EXECUTABLE);
        File testcasePath = Constant.getPathAsFile(Constant.PATH_TESTCASE);
        File reportPath = Constant.getPathAsFile(Constant.PATH_REPORT);
        File report = new File(reportPath, "report_" + df.format(date) + ".xlsx");

        ReportManager reportManager = new ReportManager();

        FileHandler fileHandler = new FileHandler();
        Document[] docs = fileHandler.organize(inputPath, outputPath);
        Map<String, Document> docMap = Document.docs2map(Arrays.asList(docs));
        System.out.println(docs.length + " documents read");

        File[] codes = outputPath.listFiles();
        if(codes == null){
            System.out.println("There is no output files");
            System.exit(3);
        }

        try {
            CGrader grader = new CGrader();
            grader.loadTestCases(testcasePath);

            for(File code : codes){
                String fileName = code.getName();
                System.out.print("Compile: " + code.getAbsolutePath());
                String r = grader.compile(code, execPath);
                if(new File(execPath, fileName.replaceAll(".c", ".exe")).exists())
                    System.out.println(" [Done]");
                else System.out.println(" [Fail]");
                r = r.replaceAll("\n", "\n\t");
                System.out.println(r);

                String id = fileName.substring(0, fileName.lastIndexOf('.'));
                docMap.get(id).msg = r;
            }

            File[] execs = execPath.listFiles();
            for(File exec : execs){
                String fileName = exec.getName();
                if(!FileHandler.getExtensionFromFileName(fileName).equals("exe")) continue;

                System.out.println(exec.getAbsolutePath());
                boolean[] results = grader.eval(exec);
                String id = fileName.substring(0, fileName.lastIndexOf('.'));
                docMap.get(id).scores = results;
                if(results == null) System.out.println("NULL");
            }

            docMap.forEach((s, doc) -> reportManager.write(doc));

            reportManager.writeToFile(report);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
