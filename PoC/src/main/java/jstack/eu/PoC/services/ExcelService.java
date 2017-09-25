package jstack.eu.PoC.services;

import jstack.eu.PoC.models.Person;
import jstack.eu.PoC.repositories.PeopleRepository;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExcelService {
    private final PeopleRepository peopleRepository;
    private static SimpleDateFormat simpleDateFormat;

    @Autowired
    public ExcelService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    public void readExcel(File file) throws IOException, ParseException {
        LinkedHashMap<String, List<String>> values;

        FileInputStream inputStream = new FileInputStream(file);

        Workbook workbook = new XSSFWorkbook(inputStream);
        Iterator<Sheet> sheets = workbook.sheetIterator();

        while (sheets.hasNext()) {
            values = new LinkedHashMap<>();

            getData(sheets.next(), values);

            List<Person> people = getPeople(values);
            for (Person p : people) {
                if (peopleRepository.findByHash(p.getHash()).size() == 0) {
                    peopleRepository.save(p);
                }
            }
        }

        workbook.close();
        inputStream.close();
    }

    private List<Person> getPeople(LinkedHashMap<String, List<String>> values) throws ParseException {
        int size = values.values().iterator().next().size();
        List<Person> people = new ArrayList<>();
        String value;
        Person person;

        for (int i = 0; i < size; i++) {
            person = new Person();
            for (String key : values.keySet()) {
                value = values.get(key).get(i);
                switch (key.toLowerCase()) {
                    case "first_name":
                        person.setFirst_name(value);
                        break;
                    case "last_name":
                        person.setLast_name(value);
                        break;
                    case "gender":
                        person.setGender(value);
                        break;
                    case "ip_address":
                        person.setIp_address(value);
                        break;
                    case "date":
                        person.setDate(simpleDateFormat.parse(value));
                        break;
                    case "age":
                        person.setAge((int) Double.parseDouble(value));
                }
            }
            person.setHash(person.hashCode());
            people.add(person);
        }
        return people;
    }

    private static void getData(Sheet firstSheet, LinkedHashMap<String, List<String>> values) {
        List<String> keys = new ArrayList<>();
        int rowCount = 0;

        for (Row nextRow : firstSheet) {
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            int columnIndex = 0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                getCellData(cell, rowCount, values, keys, columnIndex, nextRow);
                columnIndex++;
            }
            rowCount++;
        }

    }

    private static void getCellData(Cell cell, int rowCount, LinkedHashMap<String, List<String>> values, List<String> keys, int columnIndex, Row row) {
        String value = "";
        if (rowCount == 0) {
            String key = cell.getStringCellValue();
            values.put(key, new ArrayList<>());
            keys.add(key);
            return;
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            default:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    value = simpleDateFormat.format(date);
                }
        }
        values.get(keys.get(columnIndex)).add(value);
    }
}
