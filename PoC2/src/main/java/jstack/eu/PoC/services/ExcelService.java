package jstack.eu.PoC.services;

import jstack.eu.PoC.models.Person;
import jstack.eu.PoC.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class ExcelService {
    private final PeopleRepository peopleRepository;
    private static SimpleDateFormat simpleDateFormat;

    @Autowired
    public ExcelService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    public void readExcel(String file) throws Exception {
        ExcelToObjectMapper mapper = new ExcelToObjectMapper(file);
        List<Person> people = mapper.map(Person.class, 1);
        people.addAll(mapper.map(Person.class,0));
        for (Person p : people) {
            p.setHash(p.hashCode());
            if (peopleRepository.findByHash(p.getHash()).size() == 0) {
                peopleRepository.save(p);
            }
        }
        //x.setHash(x.hashCode())
    }
}

