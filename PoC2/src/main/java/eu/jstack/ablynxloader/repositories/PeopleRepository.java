package eu.jstack.ablynxloader.repositories;

import eu.jstack.ablynxloader.models.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PeopleRepository extends MongoRepository<Person, UUID> {

    List<Person> findByHash(Integer hash);
}
