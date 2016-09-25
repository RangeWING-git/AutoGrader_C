package kr.co.flywing.app;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by RangeWING on 2016-09-14.
 */
public class Constant {
    public static final String VERSION = "Indev 0.4.2";
    public static final String BUILD = "G9252";

    public static final String OS = "OS";
    public static final String OS_WIN = "win";
    public static final String OS_LINUX = "linux";
    public static final String COMPILER = "COMPILER";
    public static final String COMPILER_VC = "VC";
    public static final String COMPILER_GCC = "gcc";
    public static final String PATH_INPUT = "PATH_INPUT";
    public static final String PATH_OUTPUT = "PATH_OUTPUT";
    public static final String PATH_TESTCASE = "PATH_TESTCASE";
    public static final String PATH_EXECUTABLE = "PATH_EXECUTABLE";
    public static final String PATH_REPORT = "PATH_REPORT";
    public static final String PATH_VC = "PATH_VC";
    public static final String ROOT_DIR = "ROOT_DIR";

    private static final String SETTING_FILE = "./settings.inf";
    private static HashMap<String, String> map = new HashMap<>();

    public static void init() throws NotSupportedOSException, IOException{
        System.out.println("AutoGrader for KAIST cyber talented education");
        System.out.println(VERSION + " " + BUILD);
        System.out.println("Developed by RangeWING(rangewing@kaist.ac.kr) @ WingDev (flywing.co.kr)");

        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) {
            map.put(OS, OS_WIN);
            map.put(COMPILER, COMPILER_VC);
            System.out.println("Start windows mode: compiler = VC");
        }else if(os.contains("nix") || os.contains("nux") || os.contains("aix")){
            System.out.println("Start linux/unix mode: compiler = gcc");
            map.put("OS", OS_LINUX);
            map.put("COMPILER", COMPILER_GCC);
        }else{
            throw new NotSupportedOSException();
        }
        getSettings();
        printSettings();
    }

    protected static void getSettings() throws IOException{
        File settingFile = new File(SETTING_FILE);
        if(!settingFile.exists()) throw new IOException("No setting file");

        FileReader reader = new FileReader(settingFile);
        int c;
        StringBuilder sb = new StringBuilder();
        do {
            c = reader.read();
            if(c == '#'){ do {c = reader.read();} while(c != '\n');}
            else if(c != '\n' && c != -1) sb.append((char)c);
            else {
                if(sb.length() > 0) {
                    String line = sb.toString();
                    String[] tokens = line.split("=");
                    if(tokens.length > 1) map.put(tokens[0].trim(), tokens[1].trim());
                }
                sb = new StringBuilder();
            }
        } while(c != -1);
        reader.close();
    }

    public static File getPathAsFile(String key){
        String path = map.get(key);
        File file = new File(path);
        if(!file.exists()){
            file = new File(path.replaceFirst(".", map.get(ROOT_DIR)));
        }
        return file;
    }

    public static String get(String key){
        return map.get(key);
    }

    private static void printSettings(){
        System.out.println("Settings: ");
        Set<Map.Entry<String, String>> set = map.entrySet();
        for(Map.Entry<String, String> e : set){
            System.out.println("\t" + e.getKey() + ":\t" + e.getValue());
        }
    }

    public static boolean isEqual(String key, String str){
        return map.get(key).equals(str);
    }

    public static class NotSupportedOSException extends Exception{
        public NotSupportedOSException(){
            super("You are using not supported OS: " + System.getProperty("os.name"));
        }
    }
}
