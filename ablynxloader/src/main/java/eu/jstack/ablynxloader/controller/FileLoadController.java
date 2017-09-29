package eu.jstack.ablynxloader.controller;

import eu.jstack.ablynxloader.exception.FileLoadNotSupportedException;
import eu.jstack.ablynxloader.fileload.service.LoadService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/fileload")
public class FileLoadController {
    private final LoadService loadService;

    @Autowired
    public FileLoadController(LoadService loadService) {
        this.loadService = loadService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return new ResponseEntity<Object>(loadService.loadFile(file.getInputStream(), file.getOriginalFilename()), HttpStatus.OK);
        } catch (IOException | FileLoadNotSupportedException | ParseException | InvalidFormatException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /*@PatchMapping(value = "/{filename}/content/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateContents(@PathVariable("filename") String filename, @RequestBody Object object) {
        try {
            ArrayList<LinkedHashMap<String, Object>> values = ((LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>) object).get("values");
            FileLoadDTO fileLoadDTO = loadService.updateFileLoads(filename, values);
            return new ResponseEntity<Object>(fileLoadDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }*/

    @PatchMapping(value = "/{filename}/source/{sourcename}/content/{hash}/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateContent(@PathVariable("filename") String filename, @PathVariable("sourcename") String sourcename, @RequestBody Object object) {
        try {
            ArrayList<LinkedHashMap<String, Object>> values = ((LinkedHashMap<String, ArrayList<LinkedHashMap<String,Object>>>) object).get("values");
            return new ResponseEntity<Object>(loadService.update(filename, sourcename, values), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/{filename}/source/{sourcename}/content/insert", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> insertContent(@PathVariable("filename") String filename, @PathVariable("sourcename") String sourcename, @RequestBody Object object) {
        try {
            LinkedHashMap<String, Object> value = ((LinkedHashMap<String, LinkedHashMap<String, Object>>) object).get("value");

            return new ResponseEntity<>(loadService.insertContent(filename, sourcename, value), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{filename}/source/{sourcename}/content/{hash}/delete", produces = "application/json")
    public ResponseEntity<?> deleteContent(@PathVariable("filename") String filename, @PathVariable("sourcename") String sourcename, @PathVariable("hash") Integer[] hashes) {
        try {
            loadService.deleteContents(filename, sourcename, hashes);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /*@GetMapping(value = "/{filename}/content", produces = "application/json")
    public ResponseEntity<?> getContent(@PathVariable("filename") String filename) {
        try {
            return new ResponseEntity<>(new FileLoadDTO(loadService.getContent(filename), false), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }*/
}
