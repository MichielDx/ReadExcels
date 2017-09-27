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

    @PatchMapping(value = "/{filename}/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateContents(@PathVariable("filename") String filename, @RequestBody ArrayList<LinkedHashMap<String, Object>> values) {
        return null;
    }
}
