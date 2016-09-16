package kr.co.flywing.app;

import java.io.*;

/**
 * Created by RangeWING on 2016-09-14.
 */
public class Main {

    public static void main(String[] args){
        File inputPath = new File(Constant.INPUT_PATH);
        File outputPath = new File(Constant.OUTPUT_PATH);

        FileHandler fileHandler = new FileHandler();
        fileHandler.organize(inputPath, outputPath);
    }

}
