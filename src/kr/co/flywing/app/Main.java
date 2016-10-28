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

        int mode = Constant.MODE_PART_BASE;
        boolean gradeContain = false;

        if(args.length < 1){
            System.out.print("ARGS: ");
            Scanner sc = new Scanner(System.in);
            String str = sc.nextLine();
            sc.close();
            args = str.split(" ");
        }

        for(int i=0; i<args.length; i++) {
            String a = args[i].toLowerCase();
            if (a.equals("full")) {
                mode = Constant.MODE_TOTAL;
            } else if (a.equals("read")) {
                mode |= Constant.MODE_PART_READ_HWP;
            } else if (a.equals("compile")) {
                mode |= Constant.MODE_PART_COMPILE;
            } else if (a.equals("grade")) {
                mode |= Constant.MODE_PART_GRADE;
            } else if (a.equals("report")) {
                mode |= Constant.MODE_PART_REPORT;
            } else if (a.equals("unzip_project")) {
                System.out.println("Start unzipping project");
                (new FileHandler()).unzip_project(Constant.getPathAsFile(Constant.PATH_INPUT), Constant.getPathAsFile(Constant.PATH_OUTPUT));
            } else if (a.equals("contain")) {
                Constant.putSetting(Constant.GRADE_CONTAIN, "TRUE");
            }
        }


        AutoGrader autoGrader = new AutoGrader(mode);
        if(Constant.isKeyInBit(mode, Constant.MODE_PART_READ_HWP)) autoGrader.readHwp();
        if(Constant.isKeyInBit(mode, Constant.MODE_PART_COMPILE)) autoGrader.compile();
        if(Constant.isKeyInBit(mode, Constant.MODE_PART_GRADE)) autoGrader.grade();
        if(Constant.isKeyInBit(mode, Constant.MODE_PART_REPORT)) autoGrader.createReport();

    }

}
