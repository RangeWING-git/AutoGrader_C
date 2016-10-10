package kr.co.flywing.app;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

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
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");

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
                if(docMap.get(id) != null)
                    docMap.get(id).msg = r;
                else
                    System.err.println("cannot find info of " + id);
            }

            File[] execs = execPath.listFiles();
            List<String> outputList = new ArrayList<>();
            for(File exec : execs){
                String fileName = exec.getName();
                if(!FileHandler.getExtensionFromFileName(fileName).equals("exe")) continue;

                System.out.println(exec.getAbsolutePath());
                boolean[] results = grader.eval(exec, outputList);
                String id = fileName.substring(0, fileName.lastIndexOf('.'));
                if(docMap.get(id) != null) {
                    docMap.get(id).scores = results;
                    docMap.get(id).execOutput = outputList;
                }else{
                    System.err.println("cannot find info of " + id);
                }
                if(results == null) System.out.println("NULL");
                outputList = new ArrayList<>();
            }

            docMap.forEach((s, doc) -> reportManager.write(doc));
            if(!report.createNewFile()) System.out.println("Failed to create report");
            reportManager.writeToFile(report);
            System.out.println("Report Created");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
