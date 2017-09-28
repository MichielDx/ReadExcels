package eu.jstack.ablynxloader.fileload.repository;

import eu.jstack.ablynxloader.fileload.entity.FileLoad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileLoadRepository extends MongoRepository<FileLoad, String> {
    FileLoad findByFilename(String filename);
}
