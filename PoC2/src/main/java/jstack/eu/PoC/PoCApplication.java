package jstack.eu.PoC;

import jstack.eu.PoC.services.ExcelService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class PoCApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(PoCApplication.class, args);
        ExcelService excelService = applicationContext.getBean(ExcelService.class);

        try {
            excelService.readExcel(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
