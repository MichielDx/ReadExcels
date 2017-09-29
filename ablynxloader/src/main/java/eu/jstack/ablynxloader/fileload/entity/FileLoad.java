package eu.jstack.ablynxloader.fileload.entity;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class FileLoad {
    @Id
    private String id;
    private String filename;
    private LinkedHashMap<String, Object> metaData;
    private ArrayList<Result> results;

    public FileLoad(String filename, LinkedHashMap<String, Object> metaData, ArrayList<Result> results) {
        this.filename = filename;
        this.metaData = metaData;
        this.results = results;
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

    public ArrayList<Result> getResults() {
        return results;
    }
}
