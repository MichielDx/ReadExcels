package eu.jstack.ablynxloader.dto;

import eu.jstack.ablynxloader.fileload.entity.Result;

import java.util.ArrayList;

public class FileLoadDTO {
    private ArrayList<Result> results;
    private ArrayList<Boolean> changed;

    public FileLoadDTO(ArrayList<Result> results, ArrayList<Boolean> changed) {
        this.results = results;
        this.changed = changed;
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    public ArrayList<Boolean> getChanged() {
        return changed;
    }

    public void setChanged(ArrayList<Boolean> changed) {
        this.changed = changed;
    }
}
