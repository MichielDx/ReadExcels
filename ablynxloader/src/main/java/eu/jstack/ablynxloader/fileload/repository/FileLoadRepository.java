package eu.jstack.ablynxloader.fileload.repository;

import eu.jstack.ablynxloader.fileload.entity.FileLoad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileLoadRepository extends MongoRepository<FileLoad, String> {
    FileLoad findByFilename(String filename);

    @Query("{query:{ \"filename\" : \"People1.xlsx\"}, update:{ \"$set\" : { \"results.$[i].content.$[j]\" : { \"first_name\" : \"test\" , \"age\" : \"21\" , \"hash\" : 1331442240.0}}},arrayFilters:  [{\"i.source\": \"final\"}, {\"j.hash\":1675886337.0}]}")
    FileLoad updateBySourcenameAndHash(String sourcename, double hash);
}
