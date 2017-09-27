package eu.jstack.ablynxloader.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class FileLoadDTO {
    private ArrayList<LinkedHashMap<String, Object>> values;
    private boolean changed;

    public FileLoadDTO(ArrayList<LinkedHashMap<String, Object>> values, boolean changed) {
        this.values = values;
        this.changed = changed;
    }

    public ArrayList<LinkedHashMap<String, Object>> getValues() {
        return values;
    }

    public void setValues(ArrayList<LinkedHashMap<String, Object>> values) {
        this.values = values;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
