package eu.jstack.ablynxloader.controller;

import eu.jstack.ablynxloader.exception.FileLoadNotSupportedException;
import eu.jstack.ablynxloader.fileload.service.LoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@RestController
public class FileLoadController {
    private final LoadService loadService;

    @Autowired
    public FileLoadController(LoadService loadService) {
        this.loadService = loadService;
    }

    @PostMapping(value = "/api/upload", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile uploadfile) {

        if (uploadfile.isEmpty()) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.BAD_REQUEST);
        }

        try {
            return new ResponseEntity<Object>(loadService.loadFile(uploadfile.getInputStream(), uploadfile.getOriginalFilename()),HttpStatus.OK);
        } catch (IOException | FileLoadNotSupportedException | ParseException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
}
