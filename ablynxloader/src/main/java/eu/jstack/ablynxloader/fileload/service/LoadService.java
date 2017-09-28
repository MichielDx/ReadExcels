package eu.jstack.ablynxloader.fileload.service;

import eu.jstack.ablynxloader.dto.FileLoadDTO;
import eu.jstack.ablynxloader.exception.FileLoadNotFoundException;
import eu.jstack.ablynxloader.exception.FileLoadNotSupportedException;
import eu.jstack.ablynxloader.fileload.entity.FileLoad;
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
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public void deleteContents(String filename, Integer[] hashes) {
        BasicQuery query = new BasicQuery("{filename: '" + filename + "'}");
        Update update = new Update();
        update.pull("content", Query.query(Criteria.where("hash").in(hashes)));
        mongoTemplate.findAndModify(query, update, FileLoad.class);
    }

    public void deleteContent(String filename, Integer hash) {
        BasicQuery query = new BasicQuery("{filename: '" + filename + "', content: { $elemMatch: { hash: " + hash + " } } }");
        Update update = new Update();
        update.pull("content", Query.query(Criteria.where("hash").is(hash)));
        mongoTemplate.findAndModify(query, update, FileLoad.class);
    }

    public LinkedHashMap<String, Object> insertContent(String filename, LinkedHashMap<String, Object> value) {
        value.remove("hash");
        value.put("hash", Objects.hashCode(value.toString()));
        BasicQuery query = new BasicQuery("{filename: '" + filename + "'}");
        Update update = new Update();
        update.push("content", value);
        FileLoad fileLoad = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), FileLoad.class);
        return fileLoad.getContent().get(fileLoad.getContent().size() - 1);
    }

    public FileLoadDTO updateFileLoads(String filename, ArrayList<LinkedHashMap<String, Object>> values) {
        ArrayList<LinkedHashMap<String, Object>> updatedValues = new ArrayList<>();
        for (LinkedHashMap<String, Object> value : values) {
            updatedValues.add(update(filename, value));
        }
        return new FileLoadDTO(updatedValues, false);
    }

    public LinkedHashMap<String, Object> getByHash(String filename, Integer hash) {
        BasicQuery query = new BasicQuery("{filename: '" + filename + "', content: { $elemMatch: { hash: " + hash + " } } }", "{ 'filename' : true , 'metaData' : true , 'content.$' : true}");
        FileLoad fileLoad = mongoTemplate.findOne(query, FileLoad.class);
        if (fileLoad != null)
            return fileLoad.getContent().get(0);
        return null;
    }

    /*public LinkedHashMap<String, Object> updateByHash(String filename, LinkedHashMap<String, Object> values) {
        values.remove("hash");
        values.put("hash", Objects.hashCode(values.toString()));

        BasicQuery query = new BasicQuery("{filename: '" + filename + "'}");
        Update update = new Update();
        update.addToSet("content", values);
        mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true), FileLoad.class);

        Integer hash1 = (Integer) values.get("hash");
        return getByHash(filename, hash1);
    }*/

    public LinkedHashMap<String, Object> update(String filename, LinkedHashMap<String, Object> values) {
        Integer hash = (Integer) values.get("hash");
        values.remove("hash");
        values.put("hash", Objects.hashCode(values.toString()));

        BasicQuery query = new BasicQuery("{filename: '" + filename + "', content: { $elemMatch: { hash: " + hash + " } } }");
        Update update = new Update();
        update.set("content.$", values);
        mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true), FileLoad.class);

        Integer hash1 = (Integer) values.get("hash");
        return getByHash(filename, hash1);
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
        ArrayList<LinkedHashMap<String, Object>> values = readFile(inputStream, workbook);
        ArrayList<LinkedHashMap<String, Object>> changedValues = new ArrayList<>();

        verifyHashes(fileLoad, changedValues, values);
        if (changedValues.size() != 0) {
            return new FileLoadDTO(changedValues, true);
        }

        CoreProperties coreProperties = workbook.getProperties().getCoreProperties();
        ExtendedProperties extendedProperties = workbook.getProperties().getExtendedProperties();

        if (fileLoad == null) {
            fileLoad = new FileLoad(filename, MetaHelper.getMetaData(coreProperties, extendedProperties), values);

        }
        if (fileLoad.getContent().size() == 0) {
            fileLoad.getContent().addAll(values);
        }

        fileLoad = fileLoadRepository.save(fileLoad);

        return new FileLoadDTO(fileLoad.getContent(), false);
    }

    private void verifyHashes(FileLoad fileLoad, Collection<LinkedHashMap<String, Object>> changedValues, ArrayList<LinkedHashMap<String, Object>> values) {
        if (fileLoad != null) {
            ArrayList<LinkedHashMap<String, Object>> existingValues = fileLoad.getContent();
            for (int i = 0; i < existingValues.size(); i++) {
                if (values.size() == i) break;

                int existingHash = (int) existingValues.get(i).get("hash");
                int newHash = (int) values.get(i).get("hash");
                if (existingHash != newHash) {
                    values.get(i).replace("hash", existingHash);
                    changedValues.add(values.get(i));
                }
            }
            if (values.size() > existingValues.size()) {
                changedValues.addAll(values.subList(existingValues.size(), values.size()));
            }
        }
    }

    private ArrayList<LinkedHashMap<String, Object>> readFile(InputStream inputStream, Workbook workbook) throws IOException, ParseException {
        ArrayList<LinkedHashMap<String, Object>> values = new ArrayList<>();

        Iterator<Sheet> sheets = workbook.sheetIterator();

        while (sheets.hasNext()) {
            SheetHelper.getData(sheets.next(), values);
        }

        return values;
    }

    public ArrayList<LinkedHashMap<String, Object>> getContent(String filename) throws FileLoadNotFoundException {
        FileLoad fileLoad = fileLoadRepository.findByFilename(filename);
        if(fileLoad != null) {
            return fileLoad.getContent();
        }
        throw new FileLoadNotFoundException("FileLoad with name: "+filename+" was not found.");
    }
}
