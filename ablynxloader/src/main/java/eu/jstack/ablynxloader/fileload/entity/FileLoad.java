package eu.jstack.ablynxloader.fileload.entity;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class FileLoad {
    @Id
    private String id;
    private String filename;
    private LinkedHashMap<String, Object> metaData;
    private ArrayList<LinkedHashMap<String, Object>> content;

    public FileLoad(String filename, LinkedHashMap<String, Object> metaData, ArrayList<LinkedHashMap<String, Object>> content) {
        this.filename = filename;
        this.metaData = metaData;
        this.content = content;
    }

    public FileLoad(String filename) {
        this.filename = filename;
    }

    public FileLoad() {
    }

    public FileLoad(LinkedHashMap<String, Object> metaData) {
        this.metaData = metaData;
    }

    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public LinkedHashMap<String, Object> getMetaData() {
        return metaData;
    }

    public ArrayList<LinkedHashMap<String, Object>> getContent() {
        return content;
    }
}
