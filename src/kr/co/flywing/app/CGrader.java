package kr.co.flywing.app;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by RangeWING on 2016-09-14.
 */
public class CGrader {
    static final String linuxcmd_compile = "gcc \"%s\" -o \"%s\"";
    static final String linuxcmd_run = "\"%s\" < \"%s\"";
    static final String wincmd_compile = "cl \"%s\" /Fe \"%s\"";
    static final String wincmd_preset = "\"%s\"\\vcvars32.exe";
    static final String wincmd_run = "\"%s\" < \"%s\"";

    private TestCase[] testCases;
    private File[] testCaseFiles;

    public TestCase[] loadTestCases(File testPath) throws IOException{
        LinkedList<TestCase> list = new LinkedList<>();
        File[] tests = testPath.listFiles();
        if(tests == null) return null;
        FileReader reader = null;
        for(File tf : tests){
            reader = new FileReader(tf);
            int c;
            StringBuilder sb = new StringBuilder();
            while((c = reader.read()) != -1){
                if(c == '\n'){
                    list.add(new TestCase(sb.toString()));
                    sb = new StringBuilder();
                }else sb.append(c);
            }
            reader.close();
        }
        testCases = new TestCase[list.size()];
        list.toArray(testCases);
        testCaseFiles = new File[list.size()];

        int i = 0;
        for(TestCase testCase : list){
            File f = File.createTempFile("testcase", Integer.toString(i), testPath);
            FileWriter writer = new FileWriter(f);
            writer.write(testCase.input);
            writer.close();
            testCaseFiles[i++] = f;
        }

        return testCases;
    }

    public String compile(File file, File execPath) throws Exception{
        String fmt = Constant.COMPILER == Constant.GCC ? linuxcmd_compile : wincmd_compile;
        String fileName = file.getName();
        File execFile = new File(execPath, fileName.substring(0, fileName.lastIndexOf('.')));
        String cmd = String.format(fmt, file.getAbsolutePath(), execFile.getAbsolutePath());
        Process proc = Runtime.getRuntime().exec(cmd);
        proc.waitFor();
        InputStream is = proc.getInputStream();
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[256];
        int c = 0;
        while((c = is.read(buf)) != -1){
            sb.append(c);
        }
        is.close();
        return sb.toString();
    }

    public void eval(File file, int compiler) throws Exception{
        if(compiler == Constant.VC) preset_win();
        for(File tcf : testCaseFiles){
            System.out.println(exec(file, tcf, compiler));
        }
    }

    public String exec(File file, File testCaseFile, int compiler) throws Exception{
        if(compiler == Constant.VC) return exec_win(file, testCaseFile);
        else return exec_linux(file ,testCaseFile);
    }

    //for windows
    public String exec_win(File file, File testCaseFile) throws Exception {
        String fmt = wincmd_run;
        String cmd = String.format(fmt, file.getAbsolutePath(), testCaseFile.getAbsolutePath());
        Process proc = Runtime.getRuntime().exec(cmd);
        proc.waitFor();
        InputStream is = proc.getInputStream();
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[256];
        int c = 0;
        while((c = is.read(buf)) != -1){
            sb.append(c);
        }
        is.close();
        return sb.toString();
    }

    public String exec_linux(File file, File testCaseFile) throws Exception {
        //TODO
        return null;
    }

    public void preset_win() throws Exception{
        Process proc = Runtime.getRuntime().exec(wincmd_preset);
        proc.waitFor();
    }


    public class TestCase {
        public String input;
        public String[] output;
        public TestCase(String line){
            int sep = line.indexOf('#');
            input = line.substring(0, sep).trim();
            output = line.substring(sep+1).trim().split(" ");
        }
    }
}
