package eu.jstack.ablynxloader.fileload.service;

import eu.jstack.ablynxloader.dto.FileLoadDTO;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

@Service
public class LoadService {
    private final FileLoadRepository fileLoadRepository;

    private MongoTemplate mongoTemplate;

    @Autowired
    public LoadService(FileLoadRepository fileLoadRepository, MongoTemplate mongoTemplate) {
        this.fileLoadRepository = fileLoadRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public LinkedHashMap<String, Object> getByHash(String hash) {
        BasicQuery query = new BasicQuery("{ content: { $elemMatch: { hash: " + hash + " } } }", "{filename:true,metaData:true,content.$:true}");
        FileLoad fileLoad = mongoTemplate.findOne(query, FileLoad.class);
        if (fileLoad != null)
            return fileLoad.getContent().get(0);
        return null;
    }

    public LinkedHashMap<String, Object> updateByHash(String hash, LinkedHashMap<String, Object> values) {
        values.forEach((x, y) -> {
            BasicQuery query = new BasicQuery("{ content: { $elemMatch: { hash: " + hash + " } } }");
            Update update = new Update();
            update.set("content.$." + x, y);
            mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options(), FileLoad.class);
        });
        return null;
    }


    public FileLoadDTO loadFile(InputStream inputStream, String filename) throws FileLoadNotSupportedException, IOException, ParseException, InvalidFormatException {

        validateFile(filename);

        FileLoad fileLoad = fileLoadRepository.findByFilename(filename);
        try (OPCPackage pkg = OPCPackage.open(inputStream);
             XSSFWorkbook workbook = new XSSFWorkbook(pkg)) {
            return createFileLoad(filename, inputStream, workbook, fileLoad);
        }
    }

    //Load local file
    public FileLoadDTO loadFile(String filepath) throws FileLoadNotSupportedException, IOException, ParseException {
        File file = new File(filepath);
        String filename = file.getName();

        validateFile(filename);

        FileLoad fileLoad = fileLoadRepository.findByFilename(filename);

        try (FileInputStream inputStream = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
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
            fileLoad = fileLoadRepository.save(fileLoad);
        }

        return new FileLoadDTO(fileLoad.getContent(), false);
    }

    private void verifyHashes(FileLoad fileLoad, Collection<LinkedHashMap<String, Object>> changedValues, ArrayList<LinkedHashMap<String, Object>> values) {
        if (fileLoad != null) {
            ArrayList<LinkedHashMap<String, Object>> existingValues = fileLoad.getContent();
            for (int i = 0; i < existingValues.size(); i++) {
                int existingHash = (int) existingValues.get(i).get("hash");
                int newHash = (int) values.get(i).get("hash");
                if (existingHash != newHash) {
                    changedValues.add(values.get(i));
                }
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
}
