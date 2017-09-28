package eu.jstack.ablynxloader.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class UpdateFileLoadDTO {
    private String filename;
    private ArrayList<LinkedHashMap<String, Object>> values;

    public UpdateFileLoadDTO(String filename, ArrayList<LinkedHashMap<String, Object>> values) {
        this.filename = filename;
        this.values = values;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ArrayList<LinkedHashMap<String, Object>> getValues() {
        return values;
    }

    public void setValues(ArrayList<LinkedHashMap<String, Object>> values) {
        this.values = values;
    }
}
