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
        }


        File inputPath = new File(Constant.INPUT_PATH);
        File outputPath = new File(Constant.OUTPUT_PATH);
        File execPath = new File(Constant.EXEC_PATH);

        //FileHandler fileHandler = new FileHandler();
        //fileHandler.organize(inputPath, outputPath);
        File[] codes = outputPath.listFiles();

        try {
            CGrader grader = new CGrader();
            grader.loadTestCases(new File(Constant.TESTCASE_PATH));
            grader.preset_win();
            for(File code : codes){
                String r = grader.compile(code, execPath);
                System.out.println(r);
            }
//            File[] execs = execPath.listFiles();
//            for(File exec : execs){
//                grader.eval(exec);
//            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
