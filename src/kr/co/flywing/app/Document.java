package kr.co.flywing.app;

/**
 * Created by RangeWING on 2016-09-16.
 */
public class Document {
    String id, name;
    String desc;
    String code;
    boolean hasCode = false;

    public Document(){}
    public Document(String fileName){
        id = FileHandler.getIdFromFileName(fileName);
        name = FileHandler.getNameFromFileName(fileName);
    }

    public static Document add(Document doc1, Document doc2){
        Document doc = new Document();
        doc.id = (doc2.id == null ? doc1.id : doc2.id);
        doc.name = (doc2.name == null ? doc1.name : doc2.name);
        doc.desc = (doc2.desc == null ? doc1.desc : doc2.desc);
        doc.code = (doc2.code == null ? doc1.code : doc2.code);
        doc.hasCode = doc1.hasCode || doc2.hasCode;
        return doc;
    }

    public void replaceCodeAll(){
        code = code.replaceAll("\r", "\n").replaceAll("／", "/").replaceAll("scanf_s", "scanf");
    }

    @Override
    public String toString(){
        return "Document:\n" +
                "\tSubmitted by " + id + " (" + name + ")\n" +
                "\tDescription: " + desc +"\n" +
                "\tCode: " + code;
    }
}