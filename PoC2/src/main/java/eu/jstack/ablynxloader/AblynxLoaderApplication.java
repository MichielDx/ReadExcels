package eu.jstack.ablynxloader;

import eu.jstack.ablynxloader.services.ExcelService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AblynxLoaderApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(AblynxLoaderApplication.class, args);
        ExcelService excelService = applicationContext.getBean(ExcelService.class);

        try {
            excelService.readExcel(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
