package kr.co.flywing.app;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by RangeWING on 2016-09-14.
 */
public class CGrader {
    static final String linuxcmd_compile = "gcc \"%s\" -o \"%s\"";
    static final String linuxcmd_run = "\"%s\" < \"%s\"";

    static final String wincmd_compile = "\"%s/bin/cl.exe\" \"%s\" /Fe\"%s\"";
    static final String wincmd_preset = "\"%s/vcvarsall.bat\"";
    static final String wincmd_run = "\"%s\" < \"%s\"";

    private TestCase[] testCases;

    public TestCase[] loadTestCases(File testPath) throws IOException{
        System.out.print("Load testcases from " + testPath.getAbsolutePath());
//        File tmpPath = new File(testPath, "tmp");
//        if(tmpPath.exists()) {
//            File[] tmps = tmpPath.listFiles();
//            for(File tmp : tmps) tmp.delete();
//            tmpPath.delete();
//        }

        LinkedList<TestCase> list = new LinkedList<>();
        File[] tests = testPath.listFiles();
        if(tests == null) return null;
        FileReader reader = null;
        for(File tf : tests){
            if(tf.isDirectory()) continue;
            reader = new FileReader(tf);
            int c;
            StringBuilder sb = new StringBuilder();
            do{
                c = reader.read();
                if(c == '\n' || c == -1){
                    TestCase tc = createTestCase(sb.toString());
                    if(tc != null) list.add(tc);
                    sb = new StringBuilder();
                }else sb.append((char)c);
            }while(c != -1);
            reader.close();
        }
        testCases = new TestCase[list.size()];
        list.toArray(testCases);
        //testCaseFiles = new File[list.size()];

        //tmpPath.mkdir();
//
//        int i = 0;
//        for(TestCase testCase : list){
//            if(testCase.input == null) continue;
//            File f = File.createTempFile("testcase", Integer.toString(i), tmpPath);
//            f.deleteOnExit();
//            FileWriter writer = new FileWriter(f);
//            writer.write(testCase.input);
//            //System.out.println("testcase " + testCase.input);
//            writer.close();
//            testCaseFiles[i++] = f;
//        }

        System.out.println("\t[Done] (" + testCases.length + " cases loaded)");
        return testCases;
    }

    public String compile(File file, File execPath) throws Exception{
        String cmd;
        String fileName = file.getName();
        File execFile = new File(execPath, fileName.substring(0, fileName.lastIndexOf('.'))+".exe");
        if(Constant.isEqual(Constant.COMPILER, Constant.COMPILER_GCC)){
            cmd = String.format(linuxcmd_compile, file.getAbsolutePath(), execFile.getAbsolutePath());
        }else{
            cmd = String.format(wincmd_compile, Constant.get(Constant.PATH_VC), file.getAbsolutePath(), execFile.getAbsolutePath());
        }

        Process proc = Runtime.getRuntime().exec(cmd);
        proc.waitFor(5000, TimeUnit.MICROSECONDS);
        InputStream is = proc.getInputStream();
        InputStream es = proc.getErrorStream();
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[256];
        int n = 0;
        while((n = is.read(buf)) != -1){
            String str = new String(buf, Charset.forName("CP949"));
            sb.append(str);
        }
        sb.append("\n");
        while((n = es.read(buf)) != -1){
            String str = new String(buf, Charset.forName("CP949"));
            sb.append(str);
        }
        is.close();
        return sb.toString();
    }

    public boolean[] eval(File file) throws Exception{
        return eval(file, null);
    }
    public boolean[] eval(File file, List<String> outputList) throws Exception{
        int k = 0;
        boolean[] results = new boolean[testCases.length];
        for(TestCase tcf : testCases){
            System.out.println("Eval: " + tcf.input);
            String result = exec(file, tcf);
            if(result == null) return null;
            System.out.println(result);
            if(outputList != null) outputList.add(result);
            boolean right = true;
            String[] outputs = result.split(" |\n");

            String gradeSetting = Constant.get(Constant.GRADE_CONTAIN);

            if(gradeSetting != null && gradeSetting.toLowerCase().equals("true")) {
                //contain setting
                int i=0, j=0;
                while(i < outputs.length && j < tcf.output.length) {
                    if(outputs[i].trim().equals(tcf.output[j])) j++;
                    else i++;
                }

                right = (j == tcf.output.length);
            }else{
                if (outputs.length == tcf.output.length) {
                    for (int i = 0; i < outputs.length; i++) {
                        if (!outputs[i].trim().equals(tcf.output[i])) {
                            right = false;
                            break;
                        }
                    }
                } else right = false;
            }
            results[k++] = right;
            System.out.println(right);
        }
        return results;
    }

    public String exec(File file, TestCase testCase) throws Exception{
        if(Constant.isEqual(Constant.COMPILER, Constant.COMPILER_VC)) return exec_win(file, testCase);
        else return exec_linux(file ,testCase);
    }

    //for windows
    public String exec_win(File file, TestCase testCase) throws Exception {
        //String fmt = wincmd_run;
        if(!FileHandler.getExtensionFromFileName(file.getName()).equals("exe")) return null;
        //String cmd = String.format(fmt, file.getAbsolutePath(), testCaseFile.getAbsolutePath());
        String cmd = ""+file.getAbsolutePath()+"";
        Process proc = Runtime.getRuntime().exec(cmd);

        InputStream is = proc.getInputStream();
        InputStream es = proc.getErrorStream();
        OutputStream os = proc.getOutputStream();
        os.write(testCase.input.getBytes());
        os.write("\n\u0004\u0000".getBytes());
        os.flush();
        os.close();
        proc.waitFor(1000, TimeUnit.MICROSECONDS);

        //TODO Thread: timeout

        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[256];
        int n = 0;
        while((n = is.read(buf)) != -1){
            sb.append(new String(buf, Charset.forName("EUC-KR")));
        }
        sb.append("\n");
        while((n = es.read(buf)) != -1){
            sb.append(new String(buf, Charset.forName("EUC-KR")));
        }
        is.close();
        return sb.toString().replaceAll("�n", "\n").trim();
    }

    public String exec_linux(File file, TestCase testCase) throws Exception {
        //TODO
        return null;
    }

    public static double calcScore(boolean[] scores){
        if(scores == null) return -1;
        boolean all = true;
        double score = 0;
        for(boolean s : scores){
            if(s) score += Constant.getInt("SCORE_EXEC_EACH");
            else all = false;
        }
        if(all) score += Constant.getInt("SCORE_EXEC_BONUS");
        return score;
    }

    @Deprecated
    public void preset_win() throws Exception{
        String cmd = String.format(wincmd_preset, Constant.get(Constant.PATH_VC));
        System.out.println(cmd);
        Process proc = Runtime.getRuntime().exec(cmd);
        proc.waitFor();
    }

    public static TestCase createTestCase(String line){
        int sep = line.indexOf('#');
        if(sep < 0) return null;
        TestCase testCase = new TestCase();
        testCase.input = line.substring(0, sep).trim();
        testCase.output = line.substring(sep+1).trim().split(" ");
        return testCase;
    }


    public static class TestCase {
        public String input;
        public String[] output;
    }
}
