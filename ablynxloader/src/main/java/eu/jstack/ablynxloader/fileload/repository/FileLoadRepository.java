package eu.jstack.ablynxloader.fileload.repository;

import eu.jstack.ablynxloader.fileload.entity.FileLoad;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileLoadRepository extends MongoRepository<FileLoad, String> {
    FileLoad findByFilename(String filename);
}
