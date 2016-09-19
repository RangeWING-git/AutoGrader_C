package kr.co.flywing.app;

import java.io.*;

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


        File inputPath = Constant.getPathAsFile(Constant.PATH_INPUT);
        File outputPath = Constant.getPathAsFile(Constant.PATH_OUTPUT);
        File execPath = Constant.getPathAsFile(Constant.PATH_EXECUTABLE);
        File testcasePath = Constant.getPathAsFile(Constant.PATH_TESTCASE);

        //FileHandler fileHandler = new FileHandler();
        //fileHandler.organize(inputPath, outputPath);
        File[] codes = outputPath.listFiles();
        if(codes == null){
            System.out.println("There is no output files");
            System.exit(3);
        }

        try {
            CGrader grader = new CGrader();
            grader.loadTestCases(testcasePath);

//            for(File code : codes){
//                System.out.print("Compile: " + code.getAbsolutePath());
//                String r = grader.compile(code, execPath);
//                if(new File(execPath, code.getName().replaceAll(".c", ".exe")).exists())
//                    System.out.println(" [Done]");
//                else System.out.println(" [Fail]");
//                System.out.println(r.replaceAll("\n", "\n\t"));
//            }
            File[] execs = execPath.listFiles();
            for(File exec : execs){
                System.out.println(exec.getAbsolutePath());
                grader.eval(exec);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
