package eu.jstack.ablynxloader.fileload.service;

import com.google.gson.Gson;
import eu.jstack.ablynxloader.dto.FileLoadDTO;
import eu.jstack.ablynxloader.exception.FileLoadNotFoundException;
import eu.jstack.ablynxloader.exception.FileLoadNotSupportedException;
import eu.jstack.ablynxloader.fileload.entity.FileLoad;
import eu.jstack.ablynxloader.fileload.entity.Result;
import eu.jstack.ablynxloader.fileload.repository.FileLoadRepository;
import eu.jstack.ablynxloader.util.MetaHelper;
import eu.jstack.ablynxloader.util.SheetHelper;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.POIXMLProperties.ExtendedProperties;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

@Service
public class LoadService {
    private final FileLoadRepository fileLoadRepository;

    private MongoTemplate mongoTemplate;

    @Autowired
    public LoadService(FileLoadRepository fileLoadRepository, MongoTemplate mongoTemplate) {
        this.fileLoadRepository = fileLoadRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void deleteContents(String filename, String sourcename, Integer[] hashes) {
        BasicQuery query = new BasicQuery("{filename: '" + filename + "', results: { $elemMatch: { source: '" + sourcename + "' } } }");
        Update update = new Update();
        update.pull("results.$.content", Query.query(Criteria.where("hash").in(hashes)));
        mongoTemplate.findAndModify(query, update, FileLoad.class);
    }

    /*public void deleteContent(String filename, Integer hash) {
        BasicQuery query = new BasicQuery("{filename: '" + filename + "', content: { $elemMatch: { hash: " + hash + " } } }");
        Update update = new Update();
        update.pull("content", Query.query(Criteria.where("hash").is(hash)));
        mongoTemplate.findAndModify(query, update, FileLoad.class);
    }*/

    public LinkedHashMap<String, Object> insertContent(String filename, String sourcename, LinkedHashMap<String, Object> value) {
        String ogHash = (String) value.get("originalhash");
        Integer originalhash = null;
        if (!ogHash.isEmpty())
            originalhash = Integer.parseInt(ogHash);
        value.remove("hash");
        value.remove("originalhash");
        value.put("hash", Objects.hash(value, sourcename, filename));
        value.put("originalhash", originalhash == null ? value.get("hash") : originalhash);

        BasicQuery query = new BasicQuery("{filename: '" + filename + "', results: { $elemMatch: { source: '" + sourcename + "' } } }");
        Update update = new Update();
        update.push("results.$.content", value);
        FileLoad fileLoad = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), FileLoad.class);

        Optional<Result> resultOptional = fileLoad.getResults().stream().filter(result -> result.getSource().equals(sourcename)).findFirst();
        Result result = null;
        if (resultOptional.isPresent()) {
            result = resultOptional.get();
        }
        return result.getContent().get(result.getContent().size() - 1);
    }

    /*public FileLoadDTO updateFileLoads(String filename, ArrayList<LinkedHashMap<String, Object>> values) {
        ArrayList<LinkedHashMap<String, Object>> updatedValues = new ArrayList<>();
        for (LinkedHashMap<String, Object> value : values) {
            updatedValues.add(update(filename, value));
        }
        return new FileLoadDTO(updatedValues, false);
    }*/

    public boolean getByHash(Integer hash) {
        BasicQuery query = new BasicQuery("{\"results.content.hash\":" + hash + "}", "{'filename':1}");
        FileLoad fileLoad = mongoTemplate.findOne(query, FileLoad.class);
        return fileLoad == null;
    }

    public boolean getByOriginalHash(Integer hash) {
        BasicQuery query = new BasicQuery("{\"results.content.originalhash\":" + hash + "}", "{'filename':1}");
        FileLoad fileLoad = mongoTemplate.findOne(query, FileLoad.class);
        return fileLoad == null;
    }

    public Object update(String filename, String sourcename, ArrayList<LinkedHashMap<String, Object>> values) {
        ArrayList<LinkedHashMap<String, Object>> updatedValues = new ArrayList<>();
        for (LinkedHashMap<String, Object> value : values) {
            Integer originalhash = (Integer) value.get("originalhash");
            value.remove("hash");
            value.remove("originalhash");
            value.put("hash", Objects.hash(value, sourcename, filename));
            value.put("originalhash", originalhash);

            //TODO store original hash?
            if (getByOriginalHash(originalhash)) {
                BasicQuery query = new BasicQuery("{filename: '" + filename + "', results: { $elemMatch: { source: '" + sourcename + "' } } }");
                Update update = new Update();
                update.push("results.$.content", value);
                mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options(), FileLoad.class);
            } else {
                Gson gson = new Gson();
                ScriptOperations scriptOps = mongoTemplate.scriptOps();
                ExecutableMongoScript updateScript = new ExecutableMongoScript("db.getCollection('fileLoad').findAndModify({query:{ \"filename\" : '" + filename + "'}, update:{ \"$set\" : { \"results.$[i].content.$[j]\" : " + gson.toJson(value) + "}},arrayFilters:  [{\"i.source\": '" + sourcename + "'}, {\"j.originalhash\":" + originalhash + "}]})");
                scriptOps.execute(updateScript);
            }

            /*Integer hash1 = (Integer) value.get("hash");
            updatedValues.add(getByHash(filename, hash1, sourcename));*/
        }
        return updatedValues;
    }

    public FileLoadDTO loadFile(InputStream inputStream, String filename) throws FileLoadNotSupportedException, IOException, ParseException, InvalidFormatException {

        validateFile(filename);

        FileLoad fileLoad = fileLoadRepository.findByFilename(filename);
        try (OPCPackage pkg = OPCPackage.open(inputStream);
             XSSFWorkbook workbook = new XSSFWorkbook(pkg)) {
            return createFileLoad(filename, inputStream, workbook, fileLoad);
        }
    }

    private void validateFile(String filename) throws FileLoadNotSupportedException {
        if (!filename.split("\\.")[1].equals("xlsx")) {
            throw new FileLoadNotSupportedException("Filetype not supported.");
        }
    }

    private FileLoadDTO createFileLoad(String filename, InputStream inputStream, XSSFWorkbook workbook, FileLoad fileLoad) throws IOException, ParseException {
        ArrayList<Result> results = readFile(inputStream, workbook, filename);
        ArrayList<Result> changedValues = new ArrayList<>();
        ArrayList<Boolean> changedList = new ArrayList<>();

        verifyHashes(fileLoad, changedValues, results);


        if (valuesChanged(changedValues, changedList)) {
            return new FileLoadDTO(changedValues, changedList);
        }

        CoreProperties coreProperties = workbook.getProperties().getCoreProperties();
        ExtendedProperties extendedProperties = workbook.getProperties().getExtendedProperties();

        if (fileLoad == null) {
            fileLoad = new FileLoad(filename, MetaHelper.getMetaData(coreProperties, extendedProperties), results);

        }
        if (fileLoad.getResults().size() == 0) {
            fileLoad.getResults().addAll(results);
        }

        fileLoad = fileLoadRepository.save(fileLoad);
        return new FileLoadDTO(fileLoad.getResults(), changedList);
    }

    private boolean valuesChanged(ArrayList<Result> changedValues, ArrayList<Boolean> changedList) {
        boolean changed = false;
        for (Result changedValue : changedValues) {
            if (!changed) {
                changed = changedValue.getContent().size() != 0;
            }
            changedList.add(changed);
        }
        return changed;
    }

    private void verifyHashes(FileLoad fileLoad, ArrayList<Result> changedValues, ArrayList<Result> values) {
        if (fileLoad == null) return;
        for (Result result : values) {
            int index = values.indexOf(result);
            changedValues.add(new Result(result.getSource()));
            for (LinkedHashMap<String, Object> content : result.getContent()) {
                if (getByHash((Integer) content.get("hash"))) {
                    changedValues.get(index).getContent().add(content);
                }
            }
        }

        /*if (fileLoad != null) {
            for (Result result : fileLoad.getResults()) {
                int index = fileLoad.getResults().indexOf(result);
                changedValues.add(new Result(result.getSource()));
                ArrayList<LinkedHashMap<String, Object>> existingValues = result.getContent();
                ArrayList<LinkedHashMap<String, Object>> newValues = values.get(fileLoad.getResults().indexOf(result)).getContent();    
                for (int i = 0; i < existingValues.size(); i++) {
                    if (newValues.size() == i) {
                        changedValues.get(index).getContent().addAll(existingValues.subList(newValues.size(), existingValues.size()));
                        break;
                    }

                    double existingHash = (double) existingValues.get(i).get("hash");

                    double newHash = (double) newValues.get(i).get("hash");
                    if (existingHash != newHash) {
                        newValues.get(i).replace("hash", existingHash);
                        changedValues.get(index).getContent().add(newValues.get(i));
                    }

                }
                if (newValues.size() > existingValues.size()) {
                    changedValues.get(index).getContent().addAll(newValues.subList(existingValues.size(), newValues.size()));
                }
            }

        }*/
    }

    private ArrayList<Result> readFile(InputStream inputStream, Workbook workbook, String filename) throws IOException, ParseException {
        ArrayList<LinkedHashMap<String, Object>> values;
        ArrayList<Result> results = new ArrayList<>();

        Iterator<Sheet> sheets = workbook.sheetIterator();

        while (sheets.hasNext()) {
            Sheet sheet = sheets.next();
            values = new ArrayList<>();
            SheetHelper.getData(sheet, values, filename);
            results.add(new Result(sheet.getSheetName(), values));
        }

        return results;
    }

    public FileLoadDTO getByFilename(String filename) throws FileLoadNotFoundException {
        FileLoad fileLoad = fileLoadRepository.findByFilename(filename);
        if (fileLoad != null) {
            return new FileLoadDTO(fileLoad.getResults(), new ArrayList<>());
        }
        throw new FileLoadNotFoundException("FileLoad with name: " + filename + " was not found.");
    }
}
