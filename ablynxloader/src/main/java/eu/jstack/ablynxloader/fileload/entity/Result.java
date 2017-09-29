package eu.jstack.ablynxloader.fileload.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Result {
    private String source;
    private ArrayList<LinkedHashMap<String, Object>> content;

    public Result(String source, ArrayList<LinkedHashMap<String, Object>> content) {
        this.source = source;
        this.content = content;
    }

    public Result() {
    }

    public Result(String source) {
        this.source = source;
        this.content = new ArrayList<>();
    }

    public String getSource() {
        return source;
    }

    public ArrayList<LinkedHashMap<String, Object>> getContent() {
        return content;
    }
}
