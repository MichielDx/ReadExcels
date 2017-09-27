package eu.jstack.ablynxloader;

import eu.jstack.ablynxloader.fileload.service.LoadService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.LinkedHashMap;

@SpringBootApplication
public class AblynxLoaderApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(AblynxLoaderApplication.class, args);
        //LoadService loadService = (LoadService) applicationContext.getBean(LoadService.class);
    }
}
