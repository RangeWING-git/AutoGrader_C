package kr.co.flywing.app;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by RangeWING on 2016-10-26.
 */
public class AutoGrader {
    private int mode;

    File inputPath = Constant.getPathAsFile(Constant.PATH_INPUT);
    File outputPath = Constant.getPathAsFile(Constant.PATH_OUTPUT);
    File execPath = Constant.getPathAsFile(Constant.PATH_EXECUTABLE);
    File testcasePath = Constant.getPathAsFile(Constant.PATH_TESTCASE);

    FileHandler fileHandler = new FileHandler();
    Map<String, Document> docMap;
    CGrader grader;

    public AutoGrader(int mode) {
        this.mode = mode;
    }

    public int readHwp() {
        Document[] docs = fileHandler.organize(inputPath, outputPath);
        docMap = Document.docs2map(Arrays.asList(docs));
        System.out.println(docs.length + " documents read");
        return docs.length;
    }

    public void initDocMap() {
        docMap = new HashMap<>();
    }

    public int compile() {
        if (grader == null) grader = new CGrader();
        File[] codes = outputPath.listFiles();
        if (codes == null) {
            System.out.println("There is no C file");
            return -1;
        }
        if (docMap == null) initDocMap();


        for(File ef : execPath.listFiles()) ef.delete();

        int count = 0;
        try {
            grader.loadTestCases(testcasePath);
            for (File code : codes) {
                boolean r = (mode == Constant.MODE_TOTAL) ? compile_sub_total(code) : compile_sub_part(code);
                if (r) count++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private boolean compile_sub_total(File code) {
        String fileName = code.getName();
        System.out.print("Compile: " + code.getAbsolutePath());
        try {
            String r = grader.compile(code, execPath);
            if (new File(execPath, fileName.replaceAll(".c", ".exe")).exists())
                System.out.println(" [Done]");
            else System.out.println(" [Fail]");
            r = r.replaceAll("\n", "\n\t");
            System.out.println(r);

            String id = fileName.substring(0, fileName.lastIndexOf('.'));
            if (docMap.get(id) != null)
                docMap.get(id).msg = r;
            else {
                System.err.println("cannot find info of " + id);
                Document newDoc = new Document();
                newDoc.id = id;
                newDoc.code = FileHandler.readFile(code);
                newDoc.msg = r;
                docMap.put(id, newDoc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    private boolean compile_sub_part(File code) {
        String fileName = code.getName();
        System.out.print("Compile: " + code.getAbsolutePath());
        try {
            String r = grader.compile(code, execPath);
            if (new File(execPath, fileName.replaceAll(".c", ".exe")).exists())
                System.out.println(" [Done]");
            else System.out.println(" [Fail]");
            r = r.replaceAll("\n", "\n\t");
            System.out.println(r);

            String id = fileName.substring(0, fileName.lastIndexOf('.'));
            Document newDoc = new Document();
            newDoc.id = id;
            newDoc.code = FileHandler.readFile(code);
            newDoc.msg = r;
            docMap.put(id, newDoc);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public int grade() {
        if(docMap == null) initDocMap();
        if(grader == null) grader = new CGrader();
        File[] execs = execPath.listFiles();
        if(execs == null) return -1;
        List<String> outputList = new ArrayList<>();
        int count = 0;
        for(File exec : execs){
            try {
                String fileName = exec.getName();
                if (!FileHandler.getExtensionFromFileName(fileName).equals("exe")) continue;

                System.out.println(exec.getAbsolutePath());
                boolean[] results = grader.eval(exec, outputList);
                String id = fileName.substring(0, fileName.lastIndexOf('.'));
                if (docMap.get(id) != null) {
                    docMap.get(id).scores = results;
                    docMap.get(id).execOutput = outputList;
                } else {
                    System.err.println("cannot find info of " + id);
                    Document newDoc = new Document();
                    newDoc.id = id;
                    newDoc.scores = results;
                    newDoc.execOutput = outputList;
                    docMap.put(id, newDoc);
                }
                if (results == null) System.out.println("NULL");
                count++;
            }catch(Exception e){
                e.printStackTrace();
            }
            outputList = new ArrayList<>();
        }
        return count;
    }

    public boolean createReport() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        File reportPath = Constant.getPathAsFile(Constant.PATH_REPORT);
        File report = new File(reportPath, "report_" + df.format(date) + ".xlsx");

        ReportManager reportManager = new ReportManager();
        docMap.forEach((s, doc) -> reportManager.write(doc));
        try {
            if (!report.createNewFile()) System.out.println("Failed to create report");
            reportManager.writeToFile(report);
        }catch(IOException e){
            System.out.println("Failed to create the report");
            return false;
        }
        System.out.println("Report Created");
        return true;
    }
}