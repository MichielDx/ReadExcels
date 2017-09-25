package jstack.eu.PoC;

import jstack.eu.PoC.services.ExcelService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
public class PoCApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(PoCApplication.class, args);
        ExcelService excelService = (ExcelService) applicationContext.getBean(ExcelService.class);
        try {
            for (int i = 0; i < 100; i++) {
                excelService.readExcel(new File(args[0]));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
