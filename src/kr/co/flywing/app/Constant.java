package kr.co.flywing.app;

/**
 * Created by RangeWING on 2016-09-14.
 */
public class Constant {
    public static final String VERSION = "Indev 0.2.1";
    public static final String BUILD = "G9170";

    public static int OS, COMPILER;
    public static final int WINDOWS = 0xB0;
    public static final int LINUX = 0xB1;
    public static final int GCC = 0xC0;
    public static final int VC = 0xC1;

    public static final String INPUT_PATH = "C:\\Users\\range\\IdeaProjects\\AutoGrader\\file\\input";
    public static final String OUTPUT_PATH = "C:\\Users\\range\\IdeaProjects\\AutoGrader\\file\\output";
    public static final String EXEC_PATH = "C:\\Users\\range\\IdeaProjects\\AutoGrader\\file\\exec";
    public static final String TESTCASE_PATH = "C:\\Users\\range\\IdeaProjects\\AutoGrader\\file\\testcase";
    public static final String VC_PATH = "C:\\Program Files (x86)\\Microsoft Visual Studio 14.0\\VC\\bin";
    public static final char SEPERATOR = '\\';

    //TODO path를 setting file에서 읽어오기
    public static void init() throws NotSupportedOSException{
        String os = System.getProperty("os.name");
        if(os.contains("win")) {
            OS = WINDOWS;
            COMPILER = VC;
        }else if(os.contains("nix") || os.contains("nux") || os.contains("aix")){
            OS = LINUX;
            COMPILER = GCC;
        }else{
            throw new NotSupportedOSException();
        }
    }

    public static class NotSupportedOSException extends Exception{
        public NotSupportedOSException(){
            super("You are using not supported OS: " + System.getProperty("os.name"));
        }
    }
}
