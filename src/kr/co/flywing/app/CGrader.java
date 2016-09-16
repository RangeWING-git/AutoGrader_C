package kr.co.flywing.app;

import java.io.InputStream;

/**
 * Created by RangeWING on 2016-09-14.
 */
public class CGrader {
    String linux_cmd = "gcc %s -o %s";
    String windows_cmd = "cl %s";
    public String exec(String file){
        String cmd = Constant.COMPILER == Constant.GCC ? linux_cmd : windows_cmd;
        //Process proc = Runtime.getRuntime().exec(cmd);
        //proc.waitFor();
        //InputStream is = proc.getInputStream();
        return null;
    }
}
