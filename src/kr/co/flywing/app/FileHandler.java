package kr.co.flywing.app;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by RangeWING on 2016-09-16.
 */
public class FileHandler {
    public Document[] organize(File inputPath, File codePath){
        File[] files = inputPath.listFiles();
        LinkedList<Document> docList = new LinkedList<>();
        HashMap<String, Boolean> codeMap = new HashMap<>();
        Queue<File> fileQueue = new LinkedList<>();
        if(files == null) return null;
        //First unzip all files and move c files
        for(File file : files){
            if(isZIP(file)) {
                File[] exFiles = unzip(file, inputPath);
                if(exFiles == null) System.out.println("Error while unzip file: " + file.getName());
                else {
                    if(!file.delete()) System.out.println("Error while delete file: " + file.getName());
                    for(File eFile : exFiles){
                        String ext = getExtensionFromFileName(eFile.getName());
                        String id = getIdFromFileName(eFile.getName());
                        if(ext.equals("c") || ext.equals("cpp")){
                            codeMap.put(id, true);
                            File codeFile = new File(codePath, id + "." + ext);
                            if(!eFile.renameTo(codeFile)) System.out.println("Error while move code file: " + eFile.getName());
                        }else fileQueue.add(eFile);
                    }
                }
            }else fileQueue.add(file);
        }
        HWPHandler hwpHandler = new HWPHandler();
        for(File file : fileQueue){
            String ext = getExtensionFromFileName(file.getName());
            String id = getIdFromFileName(file.getName());
            if(!codeMap.containsKey(id) && ext.equals("hwp")){
                try {
                    Document doc = hwpHandler.getDocument(file);
                    if(doc == null) continue;
                    File code = new File(codePath, doc.id + ".c");
                    doc.replaceCodeAll();
                    writeFile(code.getAbsolutePath(), doc.code);
                    docList.add(doc);
                }catch(HWPHandler.NotHWPFileException e){
                    e.printStackTrace();
                }
            }
        }

        Document[] returnArray = new Document[docList.size()];
        docList.toArray(returnArray);
        return returnArray;
    }

    private boolean isZIP(File file){
        return file.getName().endsWith("zip");
    }
    private File[] unzip(File file, File extractPath){
        if(!isZIP(file)) return null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        ZipInputStream zis = null;
        ZipEntry zipEntry = null;

        LinkedList<File> fileList = new LinkedList<>();

        try{
            fis = new FileInputStream(file);
            zis = new ZipInputStream(fis, Charset.forName("CP949"));
            String id = getIdFromFileName(file.getName());
            String name = getNameFromFileName(file.getName());
            while((zipEntry = zis.getNextEntry()) != null){
                System.out.println("Extracting " + zipEntry.getName());
                String ext = getExtensionFromFileName(zipEntry.getName());
                File eFile = new File(extractPath, id + '_' + name + '.' + ext);
                File pDir = eFile.getParentFile();
                if(!pDir.exists()) pDir.mkdirs();
                try{
                    fos = new FileOutputStream(eFile);
                    byte[] buf = new byte[256];
                    int len = 0;
                    while((len = zis.read(buf)) != -1){
                        fos.write(buf, 0, len);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                    return null;
                }finally{
                    if(fos != null) fos.close();
                }
                fileList.add(eFile);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally{
            try {
                if (zis != null) zis.close();
                if (fis != null) fis.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        File[] returnArray = new File[fileList.size()];
        fileList.toArray(returnArray);
        return returnArray;
    }

    public static String getIdFromFileName(String fileName){
        int idx1 = fileName.indexOf('_');
        return fileName.substring(0, idx1);
    }

    public static String getNameFromFileName(String fileName){
        int idx1 = fileName.indexOf('_');
        int idx2 = fileName.indexOf('_', idx1+1);
        if(idx2 <= idx1) idx2 = fileName.length();
        return fileName.substring(idx1+1, idx2);
    }

    public static String getExtensionFromFileName(String fileName){
        return fileName.substring(fileName.lastIndexOf('.')+1);
    }
    public boolean writeFile(String file, String content){
        try {
            BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            //FileWriter fw = new FileWriter(file);
            fw.write('\ufeff');
            fw.write(content);
            fw.close();
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
