package eu.jstack.ablynxloader;

import eu.jstack.ablynxloader.exception.FileLoadNotSupportedException;
import eu.jstack.ablynxloader.fileload.service.LoadService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;

@SpringBootApplication
public class AblynxLoaderApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(AblynxLoaderApplication.class, args);
        LoadService loadService = (LoadService) applicationContext.getBean(LoadService.class);

/*        try {
            //LinkedHashMap<String, Object> temp = loadService.getByHash("1698237872");
            loadService.loadFile(args[0]);
            //loadService.testFilename(args[0]);
            //loadService.testMetaData(args[0]);
        } catch (IOException | FileLoadNotSupportedException | ParseException e) {
            e.printStackTrace();
        }*/

//        loadService.testEntity();

        /*try {
            for (int i = 0; i < 100; i++) {
                loadService.readFile(new File(args[0]));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }*/
    }
}
